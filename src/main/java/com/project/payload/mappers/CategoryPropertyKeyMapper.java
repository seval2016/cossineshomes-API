package com.project.payload.mappers;

import com.project.entity.concretes.business.Category;
import com.project.entity.concretes.business.CategoryPropertyKey;
import com.project.entity.enums.CategoryPropertyKeyType;
import com.project.exception.ResourceNotFoundException;
import com.project.payload.request.business.CategoryPropertyKeyRequest;
import com.project.payload.response.business.category.CategoryPropertyKeyResponse;
import com.project.repository.business.CategoryRepository;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class CategoryPropertyKeyMapper {

    private final CategoryRepository categoryRepository;

    // CategoryPropertyKeyRequest -> CategoryPropertyKey
    public CategoryPropertyKey mapPropertyKeyRequestToCategoryPropertyKey(CategoryPropertyKeyRequest request, Category category) {

        Category categoryId = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found for this id :: " + request.getCategoryId()));

        return CategoryPropertyKey.builder()
                .name(request.getName())
                .type(CategoryPropertyKeyType.valueOf(request.getType()))
                .category(categoryId)
                .builtIn(false)
                .build();
    }

    // CategoryPropertyKey -> CategoryPropertyKeyResponse
    public CategoryPropertyKeyResponse mapCategoryPropertyKeyToResponse(CategoryPropertyKey categoryPropertyKey) {

        return CategoryPropertyKeyResponse.builder()
                .id(categoryPropertyKey.getId())
                .name(categoryPropertyKey.getName())
                .type(categoryPropertyKey.getType().name()) // Enum'u String'e çeviriyoruz
                .builtIn(categoryPropertyKey.getBuiltIn())
                .categoryId(categoryPropertyKey.getCategory().getId()) // Category'den categoryId alıyoruz
                .build();
    }
}