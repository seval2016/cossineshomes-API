package com.project.payload.response.business;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.project.entity.concretes.business.CategoryPropertyKey;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoryPropertyKeyResponse {

    private Long id;

    private String name;

    private String type;

    private Boolean builtIn;

    private Long categoryId;
    // Constructor to convert from CategoryPropertyKey entity
    public CategoryPropertyKeyResponse(CategoryPropertyKey key) {
        this.id = key.getId();
        this.name = key.getName();
        this.type = key.getType();
        this.builtIn = key.isBuiltIn();
        this.categoryId = key.getCategory() != null ? key.getCategory().getId() : null;
    }
}
