package com.appsmith.server.repositories;

import com.appsmith.server.domains.ApiUserGroups;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface ApiUserGroupsRepository extends ReactiveCrudRepository<ApiUserGroups, String> {

    Mono<ApiUserGroups> findFirstByApiId(String apiId);
}
