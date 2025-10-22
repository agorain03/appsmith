package com.appsmith.server.domains;

import com.appsmith.external.models.BaseDomain;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "user_page_access")
@FieldNameConstants
public class UserPageAccessMap extends BaseDomain {

    @Indexed(unique = true)
    private String userId;

    private Set<String> accessiblePageIds = new LinkedHashSet<>();

    public static class Fields extends BaseDomain.Fields {}
}
