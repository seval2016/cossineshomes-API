package com.project.payload.mappers;

import com.project.entity.concretes.business.CategoryPropertyKey;
import com.project.payload.response.business.CategoryPropertyKeyResponse;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

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
