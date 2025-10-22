package com.appsmith.server.services;

import reactor.core.publisher.Mono;

/**
 * Service to authorize execution of API actions based on custom group mappings.
 */
public interface ApiExecutionAccessService {

    /**
     * @param apiId   The action id (API id).
     * @param userId  The user identifier (email).
     * @return Mono<Boolean> true if authorized, false otherwise.
     */
    Mono<Boolean> canUserExecute(String apiId, String userId);
}
