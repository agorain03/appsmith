package com.appsmith.server.services.keycloak;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class KeycloakWebClientConfig {

    @Bean("keycloakWebClient")
    public WebClient keycloakWebClient() {
        return WebClient.builder()
                .exchangeStrategies(
                        ExchangeStrategies.builder()
                                .codecs(c -> c.defaultCodecs().maxInMemorySize(512 * 1024))
                                .build()
                )
                .build();
    }
}