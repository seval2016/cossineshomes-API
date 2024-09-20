package com.project.payload.mappers;

import com.project.entity.concretes.business.CategoryPropertyKey;
import com.project.payload.response.business.category.CategoryPropertyKeyResponse;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class CategoryPropertyKeyMapper {

    // Tek bir CategoryPropertyKey'i CategoryPropertyKeyResponse'a çevirir
    public CategoryPropertyKeyResponse mapCategoryPropertyKeyToResponse(CategoryPropertyKey categoryPropertyKey) {
        return CategoryPropertyKeyResponse.builder()
                .id(categoryPropertyKey.getId())
                .name(categoryPropertyKey.getName())
                .builtIn(false)
                .build();
    }
}
