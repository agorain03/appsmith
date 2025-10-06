package com.appsmith.server.services;

import com.appsmith.server.domains.UserPageAccessMap;
import com.appsmith.server.repositories.NewPageRepository;
import com.appsmith.server.repositories.UserPageAccessRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserPageAccessServiceImpl implements UserPageAccessService {

    private final UserPageAccessRepository userPageAccessRepository;
    private final NewPageRepository newPageRepository; // Optional: used to validate page existence.

    private static final boolean VALIDATE_PAGE_EXISTS = true;

    @Override
    public Mono<UserPageAccessMap> addPageAccess(String userId, String pageId) {
        Mono<Void> validationMono = Mono.empty();

        if (VALIDATE_PAGE_EXISTS) {
            validationMono = newPageRepository
                    .findById(pageId)
                    .switchIfEmpty(Mono.error(new IllegalArgumentException("Page not found: " + pageId)))
                    .then();
        }

        return validationMono.then(userPageAccessRepository.addPageForUser(userId, pageId));
    }

    @Override
    public Mono<UserPageAccessMap> getAccessMap(String userId) {
        return userPageAccessRepository.findByUserId(userId);
    }

    @Override
    public Mono<List<UserPageAccessMap>> getAllAccessMaps() {
        return userPageAccessRepository.findAll().collectList();
    }
}
