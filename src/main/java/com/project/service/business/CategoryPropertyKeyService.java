package com.project.service.business;

import com.project.entity.concretes.business.Category;
import com.project.entity.concretes.business.CategoryPropertyKey;
import com.project.entity.enums.CategoryPropertyKeyType;
import com.project.exception.ConflictException;
import com.project.exception.ResourceNotFoundException;
import com.project.payload.mappers.CategoryMapper;
import com.project.payload.mappers.CategoryPropertyKeyMapper;
import com.project.payload.messages.ErrorMessages;
import com.project.payload.messages.SuccessMessages;
import com.project.payload.request.business.CategoryPropertyKeyRequest;
import com.project.payload.response.business.CategoryPropertyKeyResponse;
import com.project.payload.response.business.ResponseMessage;
import com.project.repository.business.CategoryPropertyKeyRepository;
import com.project.repository.business.CategoryPropertyValueRepository;
import com.project.repository.business.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryPropertyKeyService {

    private final CategoryRepository categoryRepository;
    private final CategoryPropertyKeyMapper categoryPropertyKeyMapper;
    private final CategoryPropertyKeyRepository categoryPropertyKeyRepository;
    private final CategoryPropertyValueRepository categoryPropertyValueRepository;

    //--------------------yardımcı metodlar-------------------------
    private Category findCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.CATEGORY_NOT_FOUND));
    }

    // Yardımcı metod: ID ile CategoryPropertyKey bulur
    private CategoryPropertyKey findPropertyKeyById(Long id) {
        return categoryPropertyKeyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.CATEGORY_PROPERTY_KEY_NOT_FOUND));
    }
    //---------------------------------------------

    // Kategoriye ait property keys'leri getiren metod
    public Set<CategoryPropertyKeyResponse> getCategoryPropertyKeys(Long categoryId) {
        // Kategori ID'ye göre Category bul
        Category category = findCategoryById(categoryId);

        // Kategorinin property keys'lerini al
        Set<CategoryPropertyKey> categoryPropertyKeys = category.getCategoryPropertyKeys();

        // CategoryPropertyKey nesnelerini CategoryPropertyKeyResponse'a map et
        return categoryPropertyKeyMapper.mapCategoryPropertyKeysToResponseSet(categoryPropertyKeys);
    }

    // Yeni property key oluşturma metodu
    public ResponseMessage<CategoryPropertyKeyResponse> createCategoryPropertyKeys(Long id, CategoryPropertyKeyRequest propertyKeyRequest) {
        // Kategori ID'sine göre Category'yi bul
        Category category = findCategoryById(id);

        // Kategorinin mevcut property key'lerini al
        Set<CategoryPropertyKey> existingCategoryProperties = category.getCategoryPropertyKeys();

        // Yeni property key için çakışma kontrolü yap
        for (CategoryPropertyKey key : existingCategoryProperties) {
            if (key.getName().equals(propertyKeyRequest.getName())) {
                throw new ConflictException(ErrorMessages.CATEGORY_PROPERTY_KEY_ALREADY_EXIST);
            }
        }

        // Yeni CategoryPropertyKey oluştur
        CategoryPropertyKey categoryPropertyKey = CategoryPropertyKey.builder()
                .name(propertyKeyRequest.getName())
                .type(propertyKeyRequest.getType())
                .category(category)
                .build();

        // Yeni property key'i veritabanına kaydet
        categoryPropertyKey = categoryPropertyKeyRepository.save(categoryPropertyKey);

        // Yeni property key'i mevcut property key'lere ekle
        existingCategoryProperties.add(categoryPropertyKey);
        category.setCategoryPropertyKeys(existingCategoryProperties);

        // Kategoriyi güncelle
        categoryRepository.save(category);

        // ResponseMessage oluştur ve döndür
        return ResponseMessage.<CategoryPropertyKeyResponse>builder()
                .object(categoryPropertyKeyMapper.mapCategoryPropertyKeyToResponse(categoryPropertyKey))
                .message(SuccessMessages.CATEGORY_PROPERTY_KEY_CREATED_SUCCESS)
                .httpStatus(HttpStatus.CREATED)
                .build();
    }

    // Property key güncelleme metodu
    public ResponseMessage<CategoryPropertyKeyResponse> updateCategoryPropertyKey(Long id, CategoryPropertyKeyRequest propertyKeyRequest) {
        // Property key ID'sine göre CategoryPropertyKey bul
        CategoryPropertyKey propertyKey = findPropertyKeyById(id);

        // Eğer property key builtIn ise güncelleme yapılamaz
        if (propertyKey.isBuiltIn()) {
            throw new ConflictException(ErrorMessages.CATEGORY_PROPERTY_KEY_CANNOT_UPDATE);
        }

        // Property key'in alanlarını güncelle
        propertyKey.setName(propertyKeyRequest.getName());
        propertyKey.setType(propertyKeyRequest.getType());

        // Güncellenmiş property key'i veritabanına kaydet
        CategoryPropertyKey updatedPropertyKey = categoryPropertyKeyRepository.save(propertyKey);

        // ResponseMessage ile döndür
        return ResponseMessage.<CategoryPropertyKeyResponse>builder()
                .object(categoryPropertyKeyMapper.mapCategoryPropertyKeyToResponse(updatedPropertyKey))
                .message(SuccessMessages.CATEGORY_PROPERTY_KEY_UPDATED_SUCCESS)
                .httpStatus(HttpStatus.OK)
                .build();
    }

    public ResponseMessage<CategoryPropertyKeyResponse> deleteCategoryPropertyKey(Long id) {
        // Property key ID'sine göre CategoryPropertyKey bul
        CategoryPropertyKey propertyKey = findPropertyKeyById(id);

        // Eğer property key builtIn ise silme yapılamaz
        if (propertyKey.isBuiltIn()) {
            throw new ConflictException(ErrorMessages.CATEGORY_PROPERTY_KEY_CANNOT_UPDATE);
        }

        // İlgili property key'i veritabanından sil
        categoryPropertyKeyRepository.delete(propertyKey);

        // İlgili category_property_values kayıtlarını sil
        categoryPropertyValueRepository.deleteByPropertyKeyId(id);

        // ResponseMessage oluştur ve döndür
        return ResponseMessage.<CategoryPropertyKeyResponse>builder()
                .object(categoryPropertyKeyMapper.mapCategoryPropertyKeyToResponse(propertyKey))
                .message(SuccessMessages.CATEGORY_PROPERTY_KEY_DELETED_SUCCESS)
                .httpStatus(HttpStatus.OK)
                .build();
    }


}
