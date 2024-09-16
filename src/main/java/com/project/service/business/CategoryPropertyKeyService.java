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
import com.project.payload.request.business.JsonCategoryPropertyKeyRequest;
import com.project.payload.response.business.CategoryPropertyKeyResponse;
import com.project.payload.response.business.ResponseMessage;
import com.project.repository.business.CategoryPropertyKeyRepository;
import com.project.repository.business.CategoryPropertyValueRepository;
import com.project.repository.business.CategoryRepository;
import com.project.service.helper.CategoryPropertyKeyHelper;
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
    private final CategoryPropertyKeyHelper categoryPropertyKeyHelper;
    private final CategoryService categoryService;


    // Kategoriye ait property keys'leri getiren metod
    /*public Set<CategoryPropertyKeyResponse> getCategoryPropertyKeys(Long categoryId) {
        // Kategori ID'ye göre Category bul
        Category category = categoryPropertyKeyHelper.findCategoryById(categoryId);

        // Kategorinin property keys'lerini al
        Set<CategoryPropertyKey> categoryPropertyKeys = category.getCategoryPropertyKeys();

        // CategoryPropertyKey nesnelerini CategoryPropertyKeyResponse'a map et
        return categoryPropertyKeyMapper.mapCategoryPropertyKeyToResponse();
    }*/

    // Yeni property key oluşturma metodu
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
                .category(propertyKeyRequest.getCategory())
                .categoryPropertyValues(propertyKeyRequest.getCategoryPropertyValues())
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

    // Property key güncelleme metodu
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

    public void generateCategoryPropertyKeys() {
        if (categoryPropertyKeyRepository.findAll().isEmpty()) {

            String[] housePropertyName = {"Number of Rooms and Living Rooms", "Number of Bathrooms", "Building Age", "Gross Square Meters", "Garden", "Garage", "Min. Price", "Max. Price"};
            String[] apartmentPropertyName = {"Number of Rooms and Living Rooms", "Number of Bathrooms", "Building Age", "Gross Square Meters", "Balcony", "Garage", "Min. Price", "Max. Price"};
            String[] officePropertyName = {"Number of Rooms and Living Rooms", "Number of Bathrooms", "Building Age", "Gross Square Meters", "Storage", "Garage", "Min. Price", "Max. Price"};
            String[] villaPropertyName = {"Number of Rooms and Living Rooms", "Number of Bathrooms", "Building Age", "Gross Square Meters", "Storage", "Garage", "Min. Price", "Max. Price"};
            String[] landPropertyName = {"Square Meters", "Min. Price", "Max. Price"};

            CategoryPropertyKeyType[] propertyTypes1 = {CategoryPropertyKeyType.NUMBER, CategoryPropertyKeyType.NUMBER, CategoryPropertyKeyType.NUMBER, CategoryPropertyKeyType.NUMBER, CategoryPropertyKeyType.BOOLEAN, CategoryPropertyKeyType.BOOLEAN, CategoryPropertyKeyType.DOUBLE, CategoryPropertyKeyType.DOUBLE};
            CategoryPropertyKeyType[] propertyTypes2 = {CategoryPropertyKeyType.NUMBER, CategoryPropertyKeyType.DOUBLE, CategoryPropertyKeyType.DOUBLE};

            JsonCategoryPropertyKeyRequest[] arr = new JsonCategoryPropertyKeyRequest[5];
            arr[0] = new JsonCategoryPropertyKeyRequest(1L, housePropertyName, true);
            arr[1] = new JsonCategoryPropertyKeyRequest(2L, apartmentPropertyName, true);
            arr[2] = new JsonCategoryPropertyKeyRequest(3L, officePropertyName, true);
            arr[3] = new JsonCategoryPropertyKeyRequest(4L, villaPropertyName, true);
            arr[4] = new JsonCategoryPropertyKeyRequest(5L, landPropertyName, true);

            for (JsonCategoryPropertyKeyRequest request : arr) {

                String[] propertyName = {};
                CategoryPropertyKeyType[] propertyTypes = {};


                switch (request.getId().intValue()) {
                    case 1:
                        propertyName = housePropertyName;
                        propertyTypes = propertyTypes1;
                        break;
                    case 2:
                        propertyName = apartmentPropertyName;
                        propertyTypes = propertyTypes1;
                        break;
                    case 3:
                        propertyName = officePropertyName;
                        propertyTypes = propertyTypes1;
                        break;
                    case 4:
                        propertyName = villaPropertyName;
                        propertyTypes = propertyTypes1;
                        break;
                    case 5:
                        propertyName = landPropertyName;
                        propertyTypes = propertyTypes2;
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid category ID");
                }


                Category category = categoryService.getCategoryById(request.getId());

                for (int i = 0; i < propertyName.length; i++) {
                    CategoryPropertyKey props = CategoryPropertyKey.builder()
                            .name(propertyName[i])
                            .builtIn(request.getBuiltIn())
                            .type(propertyTypes[i])
                            .category(category)
                            .build();
                    categoryPropertyKeyRepository.save(props);

                }

            }

        }
    }
}
