package com.appsmith.server.services;

import com.appsmith.server.domains.ApiUserGroups;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ApiUserGroupsMappingServiceImpl implements ApiUserGroupsMappingService {

    private final ReactiveMongoTemplate mongoTemplate;

    @Override
    public Mono<Void> upsertApiGroups(String apiId, List<String> groups) {

        Query query = Query.query(Criteria.where("apiId").is(apiId));
        Update update = new Update()
                .set("userGroups", groups == null ? List.of() : groups)
                .currentDate("updatedAt")
                .setOnInsert("apiId", apiId)
                .setOnInsert("createdAt", Instant.now());
        return mongoTemplate.upsert(query, update, ApiUserGroups.class).then();
    }
}
