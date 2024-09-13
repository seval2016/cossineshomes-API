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

    // CategoryPropertyKey nesnesinden CategoryPropertyKeyResponse oluşturur
    public Set<CategoryPropertyKeyResponse> mapCategoryPropertyKeysToResponseSet(Set<CategoryPropertyKey> categoryPropertyKeys) {
        return categoryPropertyKeys.stream()
                .map(this::mapCategoryPropertyKeyToResponse)
                .collect(Collectors.toSet());
    }

    // Tek bir CategoryPropertyKey'i CategoryPropertyKeyResponse'a çevirir
    public CategoryPropertyKeyResponse mapCategoryPropertyKeyToResponse(CategoryPropertyKey categoryPropertyKey) {
        return CategoryPropertyKeyResponse.builder()
                .id(categoryPropertyKey.getId())
                .name(categoryPropertyKey.getName())
                .type(categoryPropertyKey.getType())
                .build();
    }
}
