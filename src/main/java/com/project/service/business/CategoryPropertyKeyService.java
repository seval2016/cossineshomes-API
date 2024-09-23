package com.project.service.business;

import com.project.entity.concretes.business.Category;
import com.project.entity.concretes.business.CategoryPropertyKey;
import com.project.entity.enums.CategoryPropertyKeyType;
import com.project.exception.ConflictException;
import com.project.payload.mappers.CategoryMapper;
import com.project.payload.mappers.CategoryPropertyKeyMapper;
import com.project.payload.messages.ErrorMessages;
import com.project.payload.messages.SuccessMessages;
import com.project.payload.request.business.CategoryPropertyKeyRequest;
import com.project.payload.request.business.JsonCategoryPropertyKeyRequest;
import com.project.payload.response.business.category.CategoryPropertyKeyResponse;
import com.project.payload.response.business.ResponseMessage;
import com.project.repository.business.CategoryPropertyKeyRepository;
import com.project.repository.business.CategoryPropertyValueRepository;
import com.project.repository.business.CategoryRepository;
import com.project.service.helper.CategoryHelper;
import com.project.service.helper.CategoryPropertyKeyHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryPropertyKeyService {

    private final CategoryPropertyKeyMapper categoryPropertyKeyMapper;
    private final CategoryPropertyKeyRepository categoryPropertyKeyRepository;
    private final CategoryPropertyValueRepository categoryPropertyValueRepository;
    private final CategoryPropertyKeyHelper categoryPropertyKeyHelper;
    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;
    private final CategoryHelper categoryHelper;

    /**
     * C07
     * Verilen kategori id'ye ait propertyleri döndüren endpoint
     */
    public Set<CategoryPropertyKeyResponse> getPropertyKeysByCategory(Long id) {
        Category category = categoryHelper.findCategoryById(id);
        Set<CategoryPropertyKey> categoryPropertyKeys = categoryPropertyKeyRepository.findByCategory_Id(category.getId());

        return categoryPropertyKeys.stream()
                .map(categoryPropertyKeyMapper::mapCategoryPropertyKeyToResponse)
                .collect(Collectors.toSet());
    }

    /**
     * C08
     * Verilen kategori id'ye göre category'nin property key'ini olusturma (Path Variable ile)
     */
    public ResponseMessage<CategoryPropertyKeyResponse> createCategoryPropertyKeys(Long id, CategoryPropertyKeyRequest propertyKeyRequest) {
        // Kategori ID'sine göre Category'yi bul
        Category category = categoryPropertyKeyHelper.findCategoryById(id);

        // Kategorinin mevcut property key'lerini al
        Set<CategoryPropertyKey> existingCategoryProperties = category.getCategoryPropertyKeys();

        // Yeni property key için çakışma kontrolü yap
        for (CategoryPropertyKey key : existingCategoryProperties) {
            if (key.getName().equals(propertyKeyRequest.getName())) {
                throw new ConflictException(ErrorMessages.CATEGORY_PROPERTY_KEY_ALREADY_EXIST);
            }
        }

        // Yeni CategoryPropertyKey oluştur
        CategoryPropertyKey propertyKey = CategoryPropertyKey.builder()
                .name(propertyKeyRequest.getName())
                .type(CategoryPropertyKeyType.valueOf(propertyKeyRequest.getType().toUpperCase()))
                .category(category)
                .builtIn(propertyKeyRequest.getBuiltIn())
                .build();

        // Yeni property key'i veritabanına kaydet
        propertyKey = categoryPropertyKeyRepository.save(propertyKey);

        // Yeni property key'i mevcut property key'lere ekle
        existingCategoryProperties.add(propertyKey);
        category.setCategoryPropertyKeys(existingCategoryProperties);


        // ResponseMessage oluştur ve döndür
        return ResponseMessage.<CategoryPropertyKeyResponse>builder()
                .object(categoryPropertyKeyMapper.mapCategoryPropertyKeyToResponse(propertyKey))
                .message(SuccessMessages.CATEGORY_PROPERTY_KEY_CREATED_SUCCESS)
                .httpStatus(HttpStatus.CREATED)
                .build();
    }

    /**
     * C09
     * Verilen id'ye göre kategori özelliğini güncelleme
     */
    public ResponseMessage<CategoryPropertyKeyResponse> updateCategoryPropertyKey(Long id, CategoryPropertyKeyRequest propertyKeyRequest) {
        // Property key ID'sine göre CategoryPropertyKey bul
        CategoryPropertyKey propertyKey = categoryPropertyKeyHelper.findPropertyKeyById(id);

        // Eğer property key builtIn ise güncelleme yapılamaz
        if (propertyKey.getBuiltIn()) {
            throw new ConflictException(ErrorMessages.CATEGORY_PROPERTY_KEY_CANNOT_UPDATE);
        }

        // Güncellenmiş property key'i veritabanına kaydet
        propertyKey.setName(propertyKeyRequest.getName());
        CategoryPropertyKey updatedPropertyKey = categoryPropertyKeyRepository.save(propertyKey);

        // ResponseMessage ile döndür
        return ResponseMessage.<CategoryPropertyKeyResponse>builder()
                .object(categoryPropertyKeyMapper.mapCategoryPropertyKeyToResponse(updatedPropertyKey))
                .message(SuccessMessages.CATEGORY_PROPERTY_KEY_UPDATED_SUCCESS)
                .httpStatus(HttpStatus.OK)
                .build();
    }

    /**
     * C10
     * Verilen id'ye göre property key silme
     */
    public ResponseMessage<CategoryPropertyKeyResponse> deleteCategoryPropertyKey(Long id) {
        // Property key ID'sine göre CategoryPropertyKey bul
        CategoryPropertyKey propertyKey =categoryPropertyKeyHelper.findPropertyKeyById(id);

        // Eğer property key builtIn ise silme yapılamaz
        if (propertyKey.getBuiltIn()) {
            throw new ConflictException(ErrorMessages.CATEGORY_PROPERTY_KEY_CANNOT_UPDATE);
        }

        // İlgili property key'i veritabanından sil
        categoryPropertyKeyRepository.delete(propertyKey);

        // İlgili category_property_values kayıtlarını sil
        categoryPropertyValueRepository.deleteByCategoryPropertyKey_Id(id);

        // ResponseMessage oluştur ve döndür
        return ResponseMessage.<CategoryPropertyKeyResponse>builder()
                .object(categoryPropertyKeyMapper.mapCategoryPropertyKeyToResponse(propertyKey))
                .message(SuccessMessages.CATEGORY_PROPERTY_KEY_DELETED_SUCCESS)
                .httpStatus(HttpStatus.OK)
                .build();
    }

}
