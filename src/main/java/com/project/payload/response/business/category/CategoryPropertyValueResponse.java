package com.project.payload.response.business.category;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.project.entity.concretes.business.CategoryPropertyValue;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoryPropertyValueResponse {

    private Long id;
    private Long categoryPropertyKey;


}
