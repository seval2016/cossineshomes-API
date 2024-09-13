package com.project.payload.response.business;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.project.entity.concretes.business.CategoryPropertyKey;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoryResponse {

    private Long id;
    private String title;
    private String icon;
    private Boolean builtIn;
    private int seq;
    private String slug;
    private Boolean isActive;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private Set<CategoryPropertyKey> categoryPropertyKeys;
}
