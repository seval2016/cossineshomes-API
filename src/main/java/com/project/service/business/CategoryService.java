package com.project.service.business;

import com.project.entity.concretes.business.Category;
import com.project.entity.concretes.business.CategoryPropertyKey;
import com.project.exception.BadRequestException;
import com.project.exception.ConflictException;
import com.project.exception.ResourceNotFoundException;
import com.project.payload.mappers.CategoryMapper;
import com.project.payload.messages.ErrorMessages;
import com.project.payload.messages.SuccessMessages;
import com.project.payload.request.business.CategoryPropertyKeyRequest;
import com.project.payload.request.business.CategoryRequest;
import com.project.payload.response.business.category.CategoryPropertyKeyResponse;
import com.project.payload.response.business.category.CategoryResponse;
import com.project.payload.response.business.ResponseMessage;
import com.project.repository.business.AdvertRepository;
import com.project.repository.business.CategoryPropertyKeyRepository;
import com.project.repository.business.CategoryRepository;
import com.project.service.helper.CategoryHelper;
import com.project.service.helper.PageableHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final CategoryHelper categoryHelper;

    /**
     * C01
     * Tüm aktif kategorileri listeleme
     */
    public Page<CategoryResponse> getAllActiveCategories(String query, int page, int size, String sort, String type) {
        // Create a Pageable object with the given parameters
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(type), sort));

        // Perform the search based on the query and return the results
        return categoryRepository.findByTitleContainingAndIsActiveTrue( query, pageable)
                .map(categoryMapper::mapCategoryToCategoryResponse);
    }

    /**
     * C02
     * Tüm kategorileri çağırma
     */
    public Page<CategoryResponse> getAllCategoriesWithPageable(String query, int page, int size, String sort, String type) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(type), sort));
        Page<Category> categoryPage;

        if (query != null && !query.isEmpty()) {
            categoryPage = categoryRepository.findByTitleContaining(query, pageable);
        } else {
            categoryPage = categoryRepository.findAll(pageable);
        }

        return categoryPage.map(categoryMapper::mapCategoryToCategoryResponse);
    }

    /**
     * C03
     * id'ye göre kategori çağırma
     */
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new RuntimeException(ErrorMessages.CATEGORY_NOT_FOUND));
    }

    /**
     * C04
     * Create a new category
     */
    public ResponseMessage<CategoryResponse> createCategory(CategoryRequest categoryRequest) {
        if (categoryRepository.existsByTitle(categoryRequest.getTitle())) {
            throw new ConflictException("Category " + categoryRequest.getTitle() + " is already exist ");
        }
        Category category = categoryMapper.mapCategoryRequestToCategory(categoryRequest);
        category.generateSlug();
        Set<CategoryPropertyKey> categoryPropertyKeys = new HashSet<>();
        if (categoryRequest.getCategoryPropertyKeys() != null) {
            for (CategoryPropertyKeyRequest propertyKeyRequest : categoryRequest.getCategoryPropertyKeys()) {
                CategoryPropertyKey categoryPropertyKey = new CategoryPropertyKey();
                categoryPropertyKey.setName(propertyKeyRequest.getName());
                categoryPropertyKey.setCategory(category);  // Kategori ile ilişkilendir
                categoryPropertyKeys.add(categoryPropertyKey);
            }
        }

        // Kategori'ye CategoryPropertyKey'leri ekle
        category.setCategoryPropertyKeys(categoryPropertyKeys);

        Category createdCategory = categoryRepository.save(category);

            // ResponseMessage olarak geri döndürülüyor
                return ResponseMessage.<CategoryResponse>builder()
                        .message("Category created successfully")
                        .object(categoryMapper.mapCategoryToCategoryResponse(createdCategory))
                        .build();
            }

    /**
     * C05
     * Update a category by its ID
     */
    public ResponseMessage<CategoryResponse> updateCategoryById(Long id, CategoryRequest categoryRequest) {
        // Kategori'yi id ile bul
        Category category = categoryHelper.findCategoryById(id);

        // Eğer builtIn true ise hata fırlat
        if (category.getBuiltIn()) {
            throw new ResourceNotFoundException(ErrorMessages.CATEGORY_CANNOT_UPDATE);
        }

        // Kategoriyi gelen request ile güncelle
        category.setTitle(categoryRequest.getTitle());
        category.setIcon(categoryRequest.getIcon());
        category.setSeq(categoryRequest.getSeq());
        category.setSlug(categoryRequest.getSlug());
        category.setIsActive(categoryRequest.getIsActive());
        category.setUpdateAt(LocalDateTime.now()); // updateAt alanını günceller

        Category updatedCategory = categoryRepository.save(category);

        return ResponseMessage.<CategoryResponse>builder()
                .object(categoryMapper.mapCategoryToCategoryResponse(updatedCategory))
                .message(SuccessMessages.CATEGORY_UPDATED)
                .httpStatus(HttpStatus.OK)
                .build();
    }

    /**
     * C06
     * Id'ye göre kategori silme
     */
    public ResponseMessage<CategoryResponse> deleteCategory(Long id) {

        // Kategori'yi id ile bul
        Category category = categoryHelper.findCategoryById(id);

        // builtIn özelliği true ise silmeye izin verilmez
        if (category.getBuiltIn()) {
            throw new IllegalStateException(ErrorMessages.CATEGORY_DELETE_ERROR);
        }

        // İlişkili advert kaydı var mı kontrol et
        if (!category.getAdverts().isEmpty()) {
            throw new IllegalStateException(ErrorMessages.CATEGORY_HAS_ADVERTS);
        }
        // Kategoriyi sil
        categoryRepository.delete(category);

        // Silinen kategori bilgilerini döner
        return ResponseMessage.<CategoryResponse>builder()
                .object(categoryMapper.mapCategoryToCategoryResponse(category))
                .message(SuccessMessages.CATEGORY_DELETED)
                .httpStatus(HttpStatus.OK)
                .build();
    }

    /**
     * C11
     * Verilen kategori id'ye ait propertyleri döndüren endpoint
     */
    public ResponseMessage<CategoryResponse> findCategoryBySlug(String slug) {

        Category category = categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.CATEGORY_NOT_FOUND));

        CategoryResponse categoryResponse = categoryMapper.mapCategoryToCategoryResponse(category);

        return ResponseMessage.<CategoryResponse>builder()
                .object(categoryResponse)
                .message(SuccessMessages.CATEGORY_FOUNDED)
                .httpStatus(HttpStatus.OK)
                .build();
    }
}
