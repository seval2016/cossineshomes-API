package com.project.service.business;

import com.project.entity.concretes.business.CategoryPropertyValue;
import com.project.exception.ResourceNotFoundException;
import com.project.repository.business.CategoryPropertyValueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryPropertyValueService {


    private final CategoryPropertyValueRepository categoryPropertyValueRepository;

    //advert için method
    public CategoryPropertyValue getCategoryPropertyValueForAdvert(Object obje){
        return categoryPropertyValueRepository.findValueByName(obje).orElseThrow(()->new ResourceNotFoundException(ErrorMessages.PROPERTY_VALUE_NOT_FOUND));
    }

    public String getPropertyKeyNameByPropertyValue(Long id){
        CategoryPropertyValue categoryPropertyValue=categoryPropertyValueRepository.findById(id).orElseThrow(()->new ResourceNotFoundException(ErrorMessages.PROPERTY_VALUE_NOT_FOUND));
        return categoryPropertyValue.getCategoryPropertyKeys().getPropertyName();

    }

    public List<CategoryPropertyValue> categoryFindAllByValue(String value) {
        return categoryPropertyValueRepository.findAllByValue(value);
    }


    public void resetCategoryPropertyValueTables() {
        categoryPropertyValueRepository.deleteAll();
    }

    @Transactional
    public CategoryPropertyValue saveCategoryPropertyValue(CategoryPropertyValue categoryPropertyValue) {
        return categoryPropertyValueRepository.save(categoryPropertyValue);
    }
}
