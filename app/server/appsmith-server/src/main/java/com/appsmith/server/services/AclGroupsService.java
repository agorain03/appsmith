package com.appsmith.server.services;

import reactor.core.publisher.Mono;

import java.util.List;

public interface AclGroupsService {
    Mono<List<String>> getAllAclGroups();
}
