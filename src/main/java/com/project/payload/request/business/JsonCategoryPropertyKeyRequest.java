package com.project.payload.request.business;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class JsonCategoryPropertyKeyRequest {

    private Long id;
    private String[] propertyName;
    private Boolean builtIn;
}
