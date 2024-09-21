package com.project.payload.response.business.category;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.project.entity.concretes.business.CategoryPropertyKey;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PropertyValueResponse {

    private CategoryPropertyKey key;
    private String value;


}
