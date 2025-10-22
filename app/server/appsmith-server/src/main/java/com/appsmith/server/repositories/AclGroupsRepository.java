package com.appsmith.server.repositories;

import com.appsmith.server.domains.AclGroups;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface AclGroupsRepository extends ReactiveCrudRepository<AclGroups, String> {}
