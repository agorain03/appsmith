package com.appsmith.server.services.keycloak.dto;

import lombok.Data;

/**
 * Minimal subset of user fields needed (id/email/username).
 */
@Data
public class KeycloakUserRepresentation {
    private String id;
    private String username;
    private String email;
}