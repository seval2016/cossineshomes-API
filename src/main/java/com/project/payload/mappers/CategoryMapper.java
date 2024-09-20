package com.project.payload.mappers;

import com.project.entity.concretes.business.Category;
import com.project.entity.concretes.business.CategoryPropertyKey;
import com.project.payload.request.business.CategoryRequest;
import com.project.payload.response.business.category.CategoryPropertyKeyResponse;
import com.project.payload.response.business.category.CategoryResponse;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Data
@Component
public class CategoryMapper {
    // Category ====> DTO:

    public CategoryResponse mapCategoryToCategoryResponse(Category category){

        return CategoryResponse.builder()
                .id(category.getId())
                .title(category.getTitle())
                .icon(category.getIcon())
                .builtIn(category.isBuiltIn())
                .seq(category.getSeq())
                .slug(category.getSlug())
                .isActive(category.isActive())
                .createAt(category.getCreateAt())
                .updateAt(category.getUpdateAt())
                .categoryPropertyKeys(category.getCategoryPropertyKeys())
                .build();
    }

    // DTO ====> Category(POJO) :

    public Category mapCategoryRequestToCategory(CategoryRequest categoryRequest){

        return Category.builder()
                .title(categoryRequest.getTitle())
                .icon(categoryRequest.getIcon())
                .seq(categoryRequest.getSeq())
                .slug(categoryRequest.getSlug())
                .isActive(categoryRequest.getIsActive())
                .createAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .builtIn(false)
                .build();
    }

    //Property key pojo ---> Property key response
    public CategoryPropertyKeyResponse mapPropertyKeytoPropertyKeyResponse(CategoryPropertyKey categoryPropertyKey) {
        return CategoryPropertyKeyResponse.builder()
                .id(categoryPropertyKey.getId())
                .name(categoryPropertyKey.getName())
                .builtIn(false)
                .build();
    }
}
