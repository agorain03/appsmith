package com.appsmith.server.services;

import com.appsmith.server.domains.UserPageAccessMap;
import reactor.core.publisher.Mono;

import java.util.List;

public interface UserPageAccessService {

    Mono<UserPageAccessMap> addPageAccess(String userId, String pageId);

    Mono<UserPageAccessMap> getAccessMap(String userId);

    Mono<List<UserPageAccessMap>> getAllAccessMaps();
}
