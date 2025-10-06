package com.appsmith.server.repositories;

import com.appsmith.server.domains.UserPageAccessMap;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface UserPageAccessRepository
        extends ReactiveMongoRepository<UserPageAccessMap, String>, CustomUserPageAccessRepository {

    Mono<UserPageAccessMap> findByUserId(String userId);
}
