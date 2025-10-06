package com.appsmith.server.repositories;

import com.appsmith.server.domains.UserPageAccessMap;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import reactor.core.publisher.Mono;

import java.time.Instant;

@RequiredArgsConstructor
public class CustomUserPageAccessRepositoryImpl implements CustomUserPageAccessRepository {

    private final ReactiveMongoOperations mongoOperations;

    @Override
    public Mono<UserPageAccessMap> addPageForUser(String userId, String pageId) {
        Query query =
                Query.query(Criteria.where(UserPageAccessMap.Fields.userId).is(userId));

        Update update = new Update()
                .addToSet(UserPageAccessMap.Fields.accessiblePageIds, pageId)
                // maintain auditing fields similar to BaseDomain conventions
                .set("updatedAt", Instant.now());

        // If creating new document: set userId and createdAt
        update.setOnInsert(UserPageAccessMap.Fields.userId, userId);
        update.setOnInsert("createdAt", Instant.now());

        FindAndModifyOptions options =
                FindAndModifyOptions.options().upsert(true).returnNew(true);

        return mongoOperations.findAndModify(query, update, options, UserPageAccessMap.class);
    }
}
