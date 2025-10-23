package com.appsmith.server.services.keycloak;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Getter
@Slf4j
@Component
public class KeycloakProperties {
    private final boolean enabled;
    private final String serverUrl;
    private final String realm;
    private final String clientId;
    private final String clientSecret;
    private final long userRoleCacheTtlSeconds;

    public KeycloakProperties() {
        this.enabled = parseBoolean(env("KEYCLOAK_ENABLED"), false);
        this.serverUrl = trimToNull(env("KEYCLOAK_SERVER_URL"));
        this.realm = trimToNull(env("KEYCLOAK_REALM"));
        this.clientId = trimToNull(env("KEYCLOAK_CLIENT_ID"));
        this.clientSecret = trimToNull(env("KEYCLOAK_CLIENT_SECRET"));
        this.userRoleCacheTtlSeconds = parseLong(env("KEYCLOAK_USER_ROLE_CACHE_TTL_SECONDS"), 60L);

        if (enabled) {
            validate();
        }
    }

    private void validate() {
        if (serverUrl == null || realm == null || clientId == null || clientSecret == null) {
            log.warn("Keycloak enabled but configuration incomplete. " +
                    "serverUrl={}, realm={}, clientId={}, clientSecretPresent={}",
                    serverUrl, realm, clientId, clientSecret != null);
        }
    }

    private static String env(String name) {
        return System.getenv(name);
    }

    private static String trimToNull(String v) {
        if (v == null) return null;
        String t = v.trim();
        return t.isEmpty() ? null : t;
        }

    private static boolean parseBoolean(String raw, boolean def) {
        if (raw == null) return def;
        return switch (raw.trim().toLowerCase()) {
            case "true", "1", "yes", "y", "on" -> true;
            case "false", "0", "no", "n", "off" -> false;
            default -> def;
        };
    }

    private static long parseLong(String raw, long def) {
        if (raw == null) return def;
        try {
            return Long.parseLong(raw.trim());
        } catch (NumberFormatException e) {
            return def;
        }
    }
}
