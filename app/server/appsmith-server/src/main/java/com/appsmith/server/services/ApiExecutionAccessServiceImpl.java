package com.appsmith.server.services;

import com.appsmith.server.domains.ApiUserGroups;
import com.appsmith.server.domains.UserGroups;
import com.appsmith.server.repositories.ApiUserGroupsRepository;
import com.appsmith.server.repositories.UserGroupsRepository;
import com.appsmith.server.services.keycloak.KeycloakFeature;
import com.appsmith.server.services.keycloak.KeycloakUserRoleService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApiExecutionAccessServiceImpl implements ApiExecutionAccessService {

    private final ApiUserGroupsRepository apiUserGroupsRepository;
    private final UserGroupsRepository userGroupsRepository;
    private final KeycloakUserRoleService keycloakUserRoleService;
    private final KeycloakFeature keycloakFeature;

    @Override
    public Mono<Boolean> canUserExecute(String apiId, String userEmail) {
        if (apiId == null || userEmail == null) {
            return Mono.just(false);
        }

        Mono<ApiUserGroups> apiGroupsMono = apiUserGroupsRepository
                .findFirstByApiId(apiId)
                .defaultIfEmpty(new ApiUserGroups());

        Mono<List<String>> userGroupListMono;

        if (keycloakFeature.isEnabled()) {
            // Fetch from Keycloak realm roles
            userGroupListMono = keycloakUserRoleService
                    .getUserRealmRolesByEmail(userEmail)
                    .map(set -> List.copyOf(set));
        } else {
            // Fallback Mongo mechanism
            userGroupListMono = userGroupsRepository
                    .findFirstByUserId(userEmail)
                    .defaultIfEmpty(new UserGroups())
                    .map(u -> u.getGroup() == null ? List.of() : u.getGroup());
        }

        return Mono.zip(apiGroupsMono, userGroupListMono)
                .map(tuple -> {
                    ApiUserGroups apiUserGroups = tuple.getT1();
                    List<String> userGroups = tuple.getT2();
                    List<String> apiAllowed = apiUserGroups.getUserGroups();

                    if (apiAllowed == null || apiAllowed.isEmpty()) {
                        log.debug("apiId={} has no allowed groups configured; denying by default.", apiId);
                        return false;
                    }
                    if (userGroups == null || userGroups.isEmpty()) {
                        log.debug("User {} has no groups; denying.", userEmail);
                        return false;
                    }

                    Set<String> userSet = new HashSet<>(userGroups);
                    for (String allowed : apiAllowed) {
                        if (userSet.contains(allowed)) {
                            return true;
                        }
                    }

                    log.debug(
                            "User {} groups {} do not intersect API {} allowed groups {}",
                            userEmail,
                            userGroups,
                            apiId,
                            apiAllowed
                    );
                    return false;
                });
    }
}
