package com.appsmith.server.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddPageAccessRequest {

    @NotBlank
    private String userId;

    @NotBlank
    private String pageId;
}
