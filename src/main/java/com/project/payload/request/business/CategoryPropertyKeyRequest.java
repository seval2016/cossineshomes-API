package com.project.payload.request.business;

import com.project.entity.concretes.business.Category;
import com.project.entity.concretes.business.CategoryPropertyValue;
import lombok.Data;
import lombok.*;
import javax.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class CategoryPropertyKeyRequest {

    private String name;

    private Boolean builtIn = false;

    private Category category;

    private List<CategoryPropertyValue> categoryPropertyValues = new ArrayList<>();

}
