package com.appsmith.server.services.keycloak;

import org.springframework.stereotype.Component;

@Component
public class KeycloakFeature {
    private final KeycloakProperties props;

    public KeycloakFeature(KeycloakProperties props) {
        this.props = props;
    }

    public boolean isEnabled() {
        return props.isEnabled();
    }
}