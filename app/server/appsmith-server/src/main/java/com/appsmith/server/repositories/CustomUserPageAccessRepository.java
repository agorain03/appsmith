package com.appsmith.server.repositories;

import com.appsmith.server.domains.UserPageAccessMap;
import reactor.core.publisher.Mono;

public interface CustomUserPageAccessRepository {

    Mono<UserPageAccessMap> addPageForUser(String userId, String pageId);
}
