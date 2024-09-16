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
import com.project.payload.response.business.CategoryResponse;
import com.project.payload.response.business.ResponseMessage;
import com.project.repository.business.AdvertRepository;
import com.project.repository.business.CategoryPropertyKeyRepository;
import com.project.repository.business.CategoryRepository;
import com.project.service.helper.MethodHelper;
import com.project.service.helper.PageableHelper;
import com.project.utils.SlugUtils;
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

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final CategoryPropertyKeyRepository categoryPropertyKeyRepository;
    private final AdvertRepository advertRepository;
    private final PageableHelper pageableHelper;


    //--------------------yardımcı metodlar-------------------------


    public Category findCategoryById(Long id) {
        return categoryRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(ErrorMessages.CATEGORY_NOT_FOUND + id));
    }
    //---------------------------------------------


    public Page<CategoryResponse> getAllActiveCategories(String query, int page, int size, String sort, String type) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(type), sort));

        Page<Category> categoryPage = categoryRepository.findByTitleContainingAndIsActiveTrue(query, pageable);
        return  categoryPage.map(categoryMapper::mapCategoryToCategoryResponse);
    }

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

    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new RuntimeException(ErrorMessages.CATEGORY_NOT_FOUND));

    }

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

    public ResponseMessage<CategoryResponse> updateCategoryById(Long id, CategoryRequest categoryRequest) {
        // Kategori'yi id ile bul
        Category category = findCategoryById(id);

        // Eğer builtIn true ise hata fırlat
        if (category.isBuiltIn()) {
            throw new ResourceNotFoundException(ErrorMessages.CATEGORY_CANNOT_UPDATE);
        }

        // Kategoriyi gelen request ile güncelle
        category.setTitle(categoryRequest.getTitle());
        category.setIcon(categoryRequest.getIcon());
        category.setSeq(categoryRequest.getSeq());
        category.setSlug(categoryRequest.getSlug());
        category.setActive(categoryRequest.getIsActive());
        category.setUpdateAt(LocalDateTime.now()); // updateAt alanını günceller

        Category updatedCategory = categoryRepository.save(category);

        return ResponseMessage.<CategoryResponse>builder()
                .object(categoryMapper.mapCategoryToCategoryResponse(updatedCategory))
                .message(SuccessMessages.CATEGORY_UPDATED)
                .httpStatus(HttpStatus.OK)
                .build();
    }

    public ResponseMessage<CategoryResponse> deleteCategory(Long id) {

        // Kategori'yi id ile bul
        Category category = findCategoryById(id);

        // builtIn özelliği true ise silmeye izin verilmez
        if (category.isBuiltIn()) {
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


    //-------------------------------

    public List<Category> getCategoryByTitle(String category) {

        return categoryRepository.findByTitle(category).orElseThrow(
                () -> new BadRequestException(ErrorMessages.CATEGORY_NOT_FOUND)
        );
    }

    public void generateCategory() {
        if (categoryRepository.findAll().isEmpty()) {
            List<Category> categories = List.of(
                    Category.builder()
                            .id(1L)
                            .title("Müstakil Ev")
                            .icon("ev_icon")
                            .builtIn(true)
                            .seq(0)
                            .slug("mustakil-ev")
                            .isActive(true)
                            .build(),
                    Category.builder()
                            .id(2L)
                            .title("Apartman Dairesi")
                            .icon("dairesi_icon")
                            .builtIn(true)
                            .seq(0)
                            .slug("apartman-dairesi")
                            .isActive(true)
                            .build(),
                    Category.builder()
                            .id(3L)
                            .title("Ofis")
                            .icon("ofis_icon")
                            .builtIn(true)
                            .seq(0)
                            .slug("kelepir-ofis")
                            .isActive(true)
                            .build(),
                    Category.builder()
                            .id(4L)
                            .title("Villa")
                            .icon("villa_icon")
                            .builtIn(true)
                            .seq(0)
                            .slug("kelepir-villa")
                            .isActive(true)
                            .build(),
                    Category.builder()
                            .id(5L)
                            .title("Arsa")
                            .icon("arsa_icon")
                            .builtIn(true)
                            .seq(0)
                            .slug("kelepir-arsa")
                            .isActive(true)
                            .build()
            );

            // Kategorileri kaydet
            categoryRepository.saveAll(categories);
        }
    }

    public List<Category> getAllCategory() {
        return categoryRepository.findAll();
    }
}
