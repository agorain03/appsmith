package com.appsmith.server.services;

import com.appsmith.server.domains.ApiUserGroups;
import com.appsmith.server.domains.UserGroups;
import com.appsmith.server.repositories.ApiUserGroupsRepository;
import com.appsmith.server.repositories.UserGroupsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApiExecutionAccessServiceImpl implements ApiExecutionAccessService {

    private final ApiUserGroupsRepository apiUserGroupsRepository;
    private final UserGroupsRepository userGroupsRepository;

    @Override
    public Mono<Boolean> canUserExecute(String apiId, String userId) {
        if (apiId == null || userId == null) {
            return Mono.just(false);
        }

        Mono<ApiUserGroups> apiGroupsMono = apiUserGroupsRepository.findFirstByApiId(apiId);
        Mono<UserGroups> userGroupsMono = userGroupsRepository.findFirstByUserId(userId);

        return Mono.zip(
                        apiGroupsMono.defaultIfEmpty(new ApiUserGroups()),
                        userGroupsMono.defaultIfEmpty(new UserGroups()))
                .map(tuple -> {
                    ApiUserGroups apiUserGroups = tuple.getT1();
                    UserGroups userGroups = tuple.getT2();

                    List<String> apiAllowed = apiUserGroups.getUserGroups();
                    List<String> userGroupList = userGroups.getGroup();

                    // If api has no record -> decide policy. Here we DENY by default.
                    if (apiAllowed == null || apiAllowed.isEmpty()) {
                        log.debug("No api_user_groups entry for apiId={}, denying by default", apiId);
                        return false;
                    }

                    if (userGroupList == null || userGroupList.isEmpty()) {
                        log.debug("User {} has no groups, denying", userId);
                        return false;
                    }

                    log.debug(
                            "User {} groups {} and API {} allowed groups {}", userId, userGroupList, apiId, apiAllowed);

                    Set<String> userSet = new HashSet<>(userGroupList);
                    for (String allowed : apiAllowed) {
                        if (userSet.contains(allowed)) {
                            return true;
                        }
                    }
                    log.debug(
                            "User {} groups {} do not intersect with API {} allowed groups {}",
                            userId,
                            userGroupList,
                            apiId,
                            apiAllowed);
                    return false;
                });
    }
}
