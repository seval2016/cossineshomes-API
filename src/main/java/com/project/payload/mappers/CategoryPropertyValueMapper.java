package com.project.payload.mappers;

import com.project.entity.concretes.business.CategoryPropertyValue;
import com.project.payload.response.business.category.PropertyValueResponse;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class CategoryPropertyValueMapper {
    public PropertyValueResponse mapCategoryPropertyValueToCategoryPropertyValueResponse(CategoryPropertyValue categoryPropertyValue) {
        if (categoryPropertyValue == null) {
            return null;
        }

        return PropertyValueResponse.builder()
                .id(categoryPropertyValue.getId())  // CategoryPropertyValue entity'sinin ID'sini Response'a ekler
                .categoryPropertyKey(categoryPropertyValue.getCategoryPropertyKey() != null ? categoryPropertyValue.getCategoryPropertyKey().getId() : null)  // CategoryPropertyKey var mı kontrol eder, varsa ID'sini ekler
                .build();

    }
}