package com.appsmith.server.services.keycloak.dto;

import lombok.Data;

/**
 * Realm role representation.
 */
@Data
public class KeycloakRoleRepresentation {
    private String id;
    private String name;
    private String description;
    private Boolean composite;
    private Boolean clientRole;
    private String containerId;
}