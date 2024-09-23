package com.project.payload.mappers;

import com.project.entity.concretes.business.Advert;
import com.project.entity.concretes.business.CategoryPropertyKey;
import com.project.entity.concretes.business.CategoryPropertyValue;
import com.project.payload.request.business.CategoryPropertyValueRequest;
import com.project.payload.response.business.category.CategoryPropertyValueResponse;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class CategoryPropertyValueMapper {
    public CategoryPropertyValueResponse mapEntityToCategoryPropertyValueResponse(CategoryPropertyValue categoryPropertyValue) {
        return CategoryPropertyValueResponse.builder()
                .id(categoryPropertyValue.getId())
                .value(categoryPropertyValue.getValue())
                .advertId(categoryPropertyValue.getAdvert().getId())
                .categoryPropertyKeyId(categoryPropertyValue.getCategoryPropertyKey().getId())
                .build();
    }

    public CategoryPropertyValue mapCategoryPropertyValueResponseToEntity(CategoryPropertyValueRequest request, Advert advert, CategoryPropertyKey categoryPropertyKey) {
        return CategoryPropertyValue.builder()
                .value(request.getValue())
                .advert(advert)
                .categoryPropertyKey(categoryPropertyKey)
                .build();
    }
}