package com.project.controller.business;

import com.project.entity.concretes.business.Category;
import com.project.entity.concretes.business.CategoryPropertyKey;
import com.project.payload.request.business.CategoryPropertyKeyRequest;
import com.project.payload.request.business.CategoryRequest;
import com.project.payload.response.business.category.CategoryPropertyKeyResponse;
import com.project.payload.response.business.category.CategoryResponse;
import com.project.payload.response.business.ResponseMessage;
import com.project.service.business.CategoryPropertyKeyService;
import com.project.service.business.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryPropertyKeyService categoryPropertyKeyService;

    /**
     * C01 -> Tüm aktif kategorileri listeleme
     * http://localhost:8080/categories/?page=1&size=10&sort=date&type=asc
     */
    @GetMapping
    public ResponseEntity<Page<CategoryResponse>> getAllActiveCategories(
            @RequestParam(value = "q", required = false) String query,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @RequestParam(value = "sort", defaultValue = "categoryId") String sort,
            @RequestParam(value = "type", defaultValue = "asc") String type) {
        return ResponseEntity.ok(categoryService.getAllActiveCategories(query, page, size, sort, type));
    }

    /**
     * C02 -> Tüm kategorileri çağırma
     * http://localhost:8080/categories/?q=blabla&page=1&size=10&sort=date&type=asc
     */
    @GetMapping("/admin")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    public ResponseEntity<Page<CategoryResponse>> getAllCategoriesWithPageable(
            @RequestParam(defaultValue = "") String query,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @RequestParam(value = "sort", defaultValue = "categoryId") String sort,
            @RequestParam(value = "type", defaultValue = "asc") String type) {
        return ResponseEntity.ok(categoryService.getAllCategoriesWithPageable(query, page, size, sort, type));
    }

    /**
     * C03 -> id'ye göre kategori çağırma
     * http://localhost:8080/categories/4
     */
    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    /**
     * C04 -> Kategori oluşturma
     * http://localhost:8080/categories
     */
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    public ResponseMessage<CategoryResponse> createCategory(@RequestBody CategoryRequest categoryRequest) {
        return categoryService.createCategory(categoryRequest);
    }

    /**
     * C05 -> Id'ye göre kategori güncelleme
     * http://localhost:8080/categories/3
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    public ResponseMessage<CategoryResponse> updateCategoryById(@PathVariable("id") Long id, @Valid @RequestBody CategoryRequest categoryRequest) {
        return categoryService.updateCategoryById(id, categoryRequest);
    }

    /**
     * C06 -> Id'ye göre kategori silme
     * http://localhost:8080/categories/3
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    public ResponseMessage<CategoryResponse> deleteCategory(@PathVariable("id") Long id) {
        return categoryService.deleteCategory(id);
    }

    /**
     * C07 -> Verilen kategori id'ye ait propertyleri döndüren endpoint
     * http://localhost:8080/categories/3/properties
     */
    @GetMapping("/{id}/properties")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    public Set<CategoryPropertyKeyResponse> getCategoryPropertyKeys(@PathVariable("id") Long id) {
        return categoryPropertyKeyService.getPropertyKeysByCategory(id);
    }

    /**
     * C08 -> Verilen kategori id'ye ait property anahtarını oluşturma
     * http://localhost:8080/categories/3/properties
     */
    @PostMapping("/{id}/properties")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    public ResponseMessage<CategoryPropertyKeyResponse> createCategoryPropertyKey(@PathVariable("id") Long categoryId, @Valid @RequestBody CategoryPropertyKeyRequest propertyKeyRequest) {
        return categoryPropertyKeyService.createCategoryPropertyKeys(categoryId, propertyKeyRequest);
    }

    /**
     * C09 -> Verilen id'ye göre kategori özelliğini güncelleme
     * http://localhost:8080/categories/properties/45
     */
    @PutMapping("/properties/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    public ResponseMessage<CategoryPropertyKeyResponse> updateCategoryPropertyKey(@PathVariable("id") Long id, @Valid @RequestBody CategoryPropertyKeyRequest propertyKeyRequest) {
        return categoryPropertyKeyService.updateCategoryPropertyKey(id, propertyKeyRequest);
    }

    /**
     * C10 -> Verilen id'ye göre property key silme
     * http://localhost:8080/categories/properties/9
     */
    @DeleteMapping("/properties/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseMessage<CategoryPropertyKeyResponse> deleteCategoryPropertyKey(@PathVariable("id") Long id) {
        return categoryPropertyKeyService.deleteCategoryPropertyKey(id);
    }

    /**
     * C11 -> Slug ile kategori getirme
     * http://localhost:8080/categories/villa_havuzlu
     */
    @GetMapping("/{slug}")
    public ResponseMessage<CategoryResponse> getCategoryBySlug(@PathVariable("slug") String slug) {
        return categoryService.findCategoryBySlug(slug);
    }
}