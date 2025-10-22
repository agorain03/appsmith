package com.appsmith.server.services;

import reactor.core.publisher.Mono;

import java.util.List;

public interface ApiUserGroupsMappingService {
    Mono<Void> upsertApiGroups(String apiId, List<String> groups);
}
