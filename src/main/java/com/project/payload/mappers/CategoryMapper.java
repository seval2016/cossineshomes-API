package com.project.payload.mappers;

import com.project.entity.concretes.business.Category;
import com.project.payload.request.business.CategoryRequest;
import com.project.payload.response.business.category.CategoryResponse;
import lombok.Data;
import org.springframework.stereotype.Component;


@Data
@Component
public class CategoryMapper {

    // Category -> CategoryResponse
    public CategoryResponse mapCategoryToCategoryResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .title(category.getTitle())
                .icon(category.getIcon())
                .slug(category.getSlug())
                .isActive(category.getIsActive())
                .seq(category.getSeq())
                .builtIn(category.getBuiltIn())
                .createAt(category.getCreateAt())
                .updateAt(category.getUpdateAt())
                .build();
    }

    // CategoryRequest -> Category
    public Category mapCategoryRequestToCategory(CategoryRequest categoryRequest) {
        return Category.builder()
                .title(categoryRequest.getTitle())
                .icon(categoryRequest.getIcon())
                .slug(categoryRequest.getSlug())
                .isActive(categoryRequest.getIsActive())
                .seq(categoryRequest.getSeq())
                .builtIn(categoryRequest.getBuiltIn())
                .build();
    }

}
