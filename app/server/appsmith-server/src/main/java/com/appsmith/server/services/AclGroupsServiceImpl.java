package com.appsmith.server.services;

import com.appsmith.server.domains.AclGroups;
import com.appsmith.server.repositories.AclGroupsRepository;
import com.appsmith.server.services.keycloak.KeycloakUserRoleService;
import com.appsmith.server.services.keycloak.KeycloakProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class AclGroupsServiceImpl implements AclGroupsService {

    private final AclGroupsRepository repository;
    private final KeycloakUserRoleService keycloakUserRoleService;
    private final KeycloakProperties keycloakProperties;

    @Override
    public Mono<List<String>> getAllAclGroups() {
        if (keycloakProperties.isEnabled()) {
            log.info("Keycloak enabled: returning realm roles instead of stored ACL groups");
            return keycloakUserRoleService
                    .getAllRealmRoles()
                    .map(list -> list);
        }

        log.info("Keycloak disabled: falling back to stored ACL groups");
        return repository
                .findAll()
                .flatMapIterable(AclGroups::getGroups)
                .filter(g -> g != null && !g.isBlank())
                .collect(() -> new LinkedHashSet<String>(), Set::add)
                .map(set -> List.copyOf(set));
    }
}
