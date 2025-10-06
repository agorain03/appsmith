package com.appsmith.server.repositories;

import com.appsmith.server.domains.UserGroups;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface UserGroupsRepository extends ReactiveCrudRepository<UserGroups, String> {

    Mono<UserGroups> findFirstByUserId(String userId);
}
