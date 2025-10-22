package com.appsmith.server.controllers;

import com.appsmith.external.views.Views;
import com.appsmith.server.constants.Url;
import com.appsmith.server.dtos.AddPageAccessRequest;
import com.appsmith.server.dtos.ResponseDTO;
import com.appsmith.server.services.UserPageAccessService;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(Url.USER_ACCESS_URL)
@RequiredArgsConstructor
public class UserPageAccessController {

    private final UserPageAccessService service;

    @PostMapping
    @JsonView(Views.Public.class)
    public Mono<ResponseDTO<Map<String, Object>>> addPageAccess(@Validated @RequestBody AddPageAccessRequest request) {
        return service.addPageAccess(request.getUserId(), request.getPageId())
                .map(doc -> new ResponseDTO<>(
                        HttpStatus.OK,
                        Map.of(
                                "userId", doc.getUserId(),
                                "accessiblePageIds", doc.getAccessiblePageIds())));
    }

    @GetMapping("/{userId}")
    @JsonView(Views.Public.class)
    public Mono<ResponseDTO<Map<String, Object>>> getAccessMap(@PathVariable String userId) {
        return service.getAccessMap(userId)
                .map(doc -> new ResponseDTO<>(
                        HttpStatus.OK,
                        Map.of(
                                "userId", doc.getUserId(),
                                "accessiblePageIds", doc.getAccessiblePageIds())))
                .defaultIfEmpty(new ResponseDTO<>(
                        HttpStatus.OK, Map.of("userId", userId, "accessiblePageIds", java.util.List.of())));
    }

    @GetMapping("/all")
    @JsonView(Views.Public.class)
    public Mono<ResponseDTO<List<Map<String, Object>>>> getAllAccessMaps() {
        return service.getAllAccessMaps()
                .map(docs -> new ResponseDTO<>(
                        HttpStatus.OK,
                        docs.stream()
                                .map(doc -> Map.of(
                                        "userId", doc.getUserId(),
                                        "accessiblePageIds", doc.getAccessiblePageIds()))
                                .toList()));
    }
}
