package com.appsmith.server.controllers;

import com.appsmith.server.domains.ApiUserGroups;
import com.appsmith.server.dtos.ResponseDTO;
import com.appsmith.server.repositories.ApiUserGroupsRepository;
import com.appsmith.server.services.AclGroupsService;
import com.appsmith.server.services.ApiUserGroupsMappingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/v1/acl")
@RequiredArgsConstructor
public class AclController {

    private final AclGroupsService aclGroupsService;
    private final ApiUserGroupsMappingService apiUserGroupsMappingService;
    private final ApiUserGroupsRepository apiUserGroupsRepository;

    // GET all available ACL group names (union of all docs)
    @GetMapping("/groups")
    public Mono<ResponseDTO<List<String>>> getAclGroups() {
        return aclGroupsService.getAllAclGroups().map(list -> new ResponseDTO<>(HttpStatus.OK, list));
    }

    // GET already assigned groups for an API
    @GetMapping("/actions/{apiId}")
    public Mono<ResponseDTO<List<String>>> getApiAcl(@PathVariable String apiId) {
        return apiUserGroupsRepository
                .findFirstByApiId(apiId)
                .map(ApiUserGroups::getUserGroups)
                .defaultIfEmpty(List.of())
                .map(list -> new ResponseDTO<>(HttpStatus.OK, list));
    }

    // Request body for upsert
    public record UpsertApiAclRequest(@Valid List<String> groups) {}

    // UPSERT groups mapping for an API
    @PutMapping("/actions/{apiId}")
    public Mono<ResponseDTO<Void>> upsertApiAcl(@PathVariable String apiId, @RequestBody UpsertApiAclRequest request) {

        return apiUserGroupsMappingService
                .upsertApiGroups(apiId, request.groups())
                .thenReturn(new ResponseDTO<>(HttpStatus.OK, null));
    }
}
