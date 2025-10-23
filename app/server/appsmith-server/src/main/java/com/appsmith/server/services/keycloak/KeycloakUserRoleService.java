package com.appsmith.server.services.keycloak;

import com.appsmith.server.services.keycloak.dto.KeycloakRoleRepresentation;
import com.appsmith.server.services.keycloak.dto.KeycloakTokenResponse;
import com.appsmith.server.services.keycloak.dto.KeycloakUserRepresentation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Fetches Keycloak realm roles for a user by email using client credentials.
 * Simplified: minimal nesting, strong DTOs, explicit generics.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KeycloakUserRoleService {

    private final KeycloakProperties props;
    private final WebClient keycloakWebClient;

    private static final class CacheEntry {
        final Set<String> roles;
        final long expiresAtMillis;
        CacheEntry(Set<String> roles, long expiresAtMillis) {
            this.roles = roles;
            this.expiresAtMillis = expiresAtMillis;
        }
    }

    private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();

    public Mono<Set<String>> getUserRealmRolesByEmail(String email) {
        log.info("[keycloakUserRoleService] isEnabled {}", props.isEnabled());
        if (!props.isEnabled()) return Mono.just(Set.of());
        if (email == null || email.isBlank()) return Mono.just(Set.of());

        long now = System.currentTimeMillis();
        CacheEntry cached = cache.get(email);
        if (cached != null && cached.expiresAtMillis > now) {
            return Mono.just(cached.roles);
        }

        return fetchAccessToken()
                .flatMap(token ->
                        findUserIdByEmail(email, token)
                                .flatMap(optUserId ->
                                        optUserId.map(userId -> fetchRealmRoleNames(userId, token))
                                                 .orElseGet(() -> Mono.just(Set.of()))
                                )
                )
                .doOnNext(roles -> cache.put(email,
                        new CacheEntry(roles, now + props.getUserRoleCacheTtlSeconds() * 1000)))
                .onErrorResume(err -> {
                    log.warn("Keycloak role lookup failed for {}: {}", email, err.getMessage());
                    return Mono.just(Set.of());
                });
    }

    /* -------- Token -------- */

    private Mono<String> fetchAccessToken() {
        String tokenUrl = props.getServerUrl()
                + "/realms/" + props.getRealm()
                + "/protocol/openid-connect/token";

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "client_credentials");
        form.add("client_id", props.getClientId());
        form.add("client_secret", props.getClientSecret());
        log.info("client_id : {} : client_secret : {}", props.getClientId(), props.getClientSecret());

        return keycloakWebClient.post()
                .uri(tokenUrl)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(form)
                .retrieve()
                .onStatus(HttpStatusCode::isError, resp ->
                        resp.bodyToMono(String.class)
                                .defaultIfEmpty("")
                                .flatMap(body -> {
                                    log.warn("Token endpoint error {} body={}", resp.statusCode(), body);
                                    return Mono.error(new RuntimeException("Keycloak token fetch failed"));
                                }))
                .bodyToMono(KeycloakTokenResponse.class)
                .map(KeycloakTokenResponse::getAccessToken)
                .doOnNext(t -> log.debug("Fetched client_credentials token at {} with token {}", Instant.now(), t));
    }

    /* -------- User ID by email -------- */

    private Mono<Optional<String>> findUserIdByEmail(String email, String bearerToken) {
        String url = props.getServerUrl()
                + "/admin/realms/" + props.getRealm()
                + "/users?email=" + email;

        log.info("Fetching user ID for email={} from Keycloak with URL = {} and bearer = {}", email, url, bearerToken);

        return keycloakWebClient.get()
                .uri(url)
                .headers(h -> h.setBearerAuth(bearerToken))
                .retrieve()
                .onStatus(HttpStatusCode::isError, resp ->
                        resp.bodyToMono(String.class)
                                .defaultIfEmpty("")
                                .flatMap(body -> {
                                    log.warn("User search error {} email={} body={}",
                                            resp.statusCode(), email, body);
                                    return Mono.error(new RuntimeException("Keycloak user search failed"));
                                }))
                .bodyToFlux(KeycloakUserRepresentation.class)
                .filter(u -> {
                    String candidate = Optional.ofNullable(u.getEmail()).orElse(u.getUsername());
                    return candidate != null && candidate.equalsIgnoreCase(email);
                })
                .take(1)
                .singleOrEmpty()
                .map(KeycloakUserRepresentation::getId)
                .map(Optional::of)
                .defaultIfEmpty(Optional.empty())
                .doOnNext(opt -> {
                    if (opt.isEmpty()) {
                        log.debug("No userId found for email={}", email);
                    }
                });
    }

    /* -------- Realm roles for user -------- */

    private Mono<Set<String>> fetchRealmRoleNames(String userId, String bearerToken) {
        String url = props.getServerUrl()
                + "/admin/realms/" + props.getRealm()
                + "/users/" + userId + "/role-mappings/realm";

        return keycloakWebClient.get()
                .uri(url)
                .headers(h -> h.setBearerAuth(bearerToken))
                .retrieve()
                .onStatus(HttpStatusCode::isError, resp ->
                        resp.bodyToMono(String.class)
                                .defaultIfEmpty("")
                                .flatMap(body -> {
                                log.warn("Role mapping error {} userId={} body={}",
                                        resp.statusCode(), userId, body);
                                return Mono.error(new RuntimeException("Keycloak role mapping failed"));
                                }))
                .bodyToFlux(KeycloakRoleRepresentation.class)
                .map(KeycloakRoleRepresentation::getName)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet()) 
                .doOnNext(set -> log.debug("Fetched roles {} for userId={}", set, userId));
    }
}