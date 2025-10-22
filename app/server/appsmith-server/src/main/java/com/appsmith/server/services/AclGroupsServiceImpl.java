package com.appsmith.server.services;

import com.appsmith.server.domains.AclGroups;
import com.appsmith.server.repositories.AclGroupsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AclGroupsServiceImpl implements AclGroupsService {

    private final AclGroupsRepository repository;

    @Override
    public Mono<List<String>> getAllAclGroups() {
        return repository
                .findAll()
                .flatMapIterable(AclGroups::getGroups)
                .filter(g -> g != null && !g.isBlank())
                .collect(() -> new LinkedHashSet<String>(), Set::add)
                .map(set -> List.copyOf(set));
    }
}
