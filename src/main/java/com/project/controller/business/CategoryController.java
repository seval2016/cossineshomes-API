package com.project.controller.business;

import com.project.entity.concretes.business.Category;
import com.project.payload.request.business.CategoryPropertyKeyRequest;
import com.project.payload.request.business.CategoryRequest;
import com.project.payload.response.business.CategoryPropertyKeyResponse;
import com.project.payload.response.business.CategoryResponse;
import com.project.payload.response.business.ResponseMessage;
import com.project.service.business.CategoryPropertyKeyService;
import com.project.service.business.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Set;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryPropertyKeyService categoryPropertyKeyService;

    //--> C01 - Tüm aktif kategorileri listeleme
    @GetMapping // http://localhost:8080/categories?page=1&size=10&sort=date&type=asc
    public ResponseEntity<Page<CategoryResponse>> getAllActiveCategories(
            @RequestParam(defaultValue = "") String query,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @RequestParam(value = "sort", defaultValue = "id") String sort,
            @RequestParam(value = "type", defaultValue = "asc") String type) {
        Page<CategoryResponse> allCategories = categoryService.getAllActiveCategories(query, page, size, sort, type);
        return ResponseEntity.ok(allCategories);
    }

    //--> C02 - Tüm kategorileri çağırma
    @GetMapping("/admin") // http://localhost:8080/categories/admin?q=blabla&page=1&size=10&sort=date&type=asc
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    public ResponseEntity<Page<CategoryResponse>> getAllCategoriesWithPageable(
            @RequestParam(defaultValue = "") String query,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @RequestParam(value = "sort", defaultValue = "id") String sort,
            @RequestParam(value = "type", defaultValue = "asc") String type) {
        Page<CategoryResponse> categories = categoryService.getAllCategoriesWithPageable(query, page, size, sort, type);
        return ResponseEntity.ok(categories);
    }

    //--> C03 - id'ye göre kategori çağırma
    @GetMapping("/{id}") // http://localhost:8080/categories/4
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    //--> C04 - Kategori oluşturma
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')") // http://localhost:8080/categories
    public ResponseMessage<CategoryResponse> createCategory(@RequestBody @Valid CategoryRequest categoryRequest) {
        return categoryService.createCategory(categoryRequest);
    }

    //--> C05 - Id'ye göre kategori güncelleme
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')") // http://localhost:8080/categories/3
    public ResponseMessage<CategoryResponse> updateCategoryById(
            @PathVariable("id") Long id,
            @Valid @RequestBody CategoryRequest categoryRequest) {
        return categoryService.updateCategoryById(id, categoryRequest);
    }

    //--> C06 - Id'ye göre kategori silme
    @DeleteMapping("/{id}") // http://localhost:8080/categories/3
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    public ResponseMessage<CategoryResponse> deleteCategory(@PathVariable("id") Long id) {
        return categoryService.deleteCategory(id);
    }

    //--> C07 - Verilen kategori id'ye ait propertyleri döndüren endpoint
    @GetMapping("/{id}/properties") // http://localhost:8080/categories/3/properties
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    public Set<CategoryPropertyKeyResponse> getCategoryPropertyKeys(@PathVariable("id") Long id) {
        return categoryPropertyKeyService.getCategoryPropertyKeys(id);
    }

    //--> C08 - Verilen kategori id'ye ait property anahtarını oluşturma
    @PostMapping("/{id}/properties") // http://localhost:8080/categories/3/properties
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    public ResponseMessage<CategoryPropertyKeyResponse> createCategoryPropertyKey(
            @PathVariable("id") Long categoryId,
            @Valid @RequestBody CategoryPropertyKeyRequest propertyKeyRequest) {
        return categoryPropertyKeyService.createCategoryPropertyKeys(categoryId, propertyKeyRequest);
    }

    //--> C09 - Verilen id'ye göre kategori özelliğini güncelleme
    @PutMapping("/properties/{id}") // http://localhost:8080/categories/properties/45
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    public ResponseMessage<CategoryPropertyKeyResponse> updateCategoryPropertyKey(
            @PathVariable("id") Long id,
            @Valid @RequestBody CategoryPropertyKeyRequest propertyKeyRequest) {
        return categoryPropertyKeyService.updateCategoryPropertyKey(id, propertyKeyRequest);
    }

    //--> C10 - Verilen id'ye göre property key silme
    @DeleteMapping("/properties/{id}") // http://localhost:8080/categories/properties/9
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseEntity<ResponseMessage<CategoryPropertyKeyResponse>> deleteCategoryPropertyKey(
            @PathVariable("id") Long id) {
        ResponseMessage<CategoryPropertyKeyResponse> responseMessage = categoryPropertyKeyService.deleteCategoryPropertyKey(id);
        return ResponseEntity.ok(responseMessage);
    }

    // C11 - Slug ile kategori getirme
    @GetMapping("/{slug}")// http://localhost:8080/categories/villa_havuzlu
    public ResponseMessage<CategoryResponse> getCategoryBySlug(@PathVariable("slug") String slug) {
        return categoryService.findCategoryBySlug(slug);
    }
}