package com.project.payload.request.business;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class JsonCategoryPropertyKeyRequest {

    private Long id;
    private String[] propertyName;
    private Boolean builtIn;
}
