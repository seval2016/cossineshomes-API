package com.project.controller.business;

import com.project.payload.request.business.AdvertRequest;
import com.project.payload.response.business.*;
import com.project.payload.response.business.advert.AdvertDetailsForSlugResonse;
import com.project.payload.response.business.advert.AdvertListResponse;
import com.project.payload.response.business.advert.AdvertResponse;
import com.project.payload.response.business.advert.CityAdvertResponse;
import com.project.payload.response.business.category.CategoryAdvertResponse;
import com.project.service.business.AdvertService;
import com.project.service.helper.AdvertHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;


@RestController
@RequestMapping("/adverts")
@RequiredArgsConstructor
public class AdvertController {

    private final AdvertService advertService;
    private final AdvertHelper advertHelper;

    // --> A01 - Belirli filtreleme kriterlerine göre ilanları getirir.
    @GetMapping
    public ResponseEntity<Page<AdvertListResponse>> getAdverts(
            @RequestParam(value = "q", required = false) String query,
            @RequestParam(value = "category_id", required = false) Long categoryId,
            @RequestParam(value = "advert_type_id", required = false) Long advertTypeId,
            @RequestParam(value = "price_start", required = false) BigDecimal priceStart,
            @RequestParam(value = "price_end", required = false) BigDecimal priceEnd,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @RequestParam(value = "sort", defaultValue = "categoryId") String sortBy,
            @RequestParam(value = "type", defaultValue = "asc") String sortDirection) {

        Page<AdvertListResponse> adverts = advertService.getAdverts(query, categoryId, advertTypeId, priceStart, priceEnd, status, page, size, sortBy, sortDirection);
        return ResponseEntity.ok(adverts);
    }

    // --> A02 - İlanlar şehre göre gruplanarak sadece aktif ilanların sayısı döndürülür.
    @GetMapping("/cities")
    @PreAuthorize("permitAll()") // http://localhost:8080/adverts/cities
    public ResponseEntity<List<CityAdvertResponse>> getAdvertsGroupedByCities() {
        return ResponseEntity.ok(advertService.getAdvertsGroupedByCities());
    }

    // --> A03 - Her kategoriye ait reklam sayısını (amount) ve category ismini döndürme
    @GetMapping("/categories")
    @PreAuthorize("permitAll()") //http://localhost:8080/adverts/categories
    public ResponseEntity<List<CategoryAdvertResponse>> getAdvertsGroupedByCategory() {
        return ResponseEntity.ok(advertService.getAdvertsGroupedByCategory());
    }

    // --> A04 - Populer ilanları almak için
    @GetMapping("/popular/{amount}")
    @PreAuthorize("permitAll()") //http://localhost:8080/adverts/popular/20
    public ResponseEntity<Page<AdvertResponse>> getMostPopularAdverts(@PathVariable(value = "amount", required = false) Integer amount) {
        Pageable pageable = PageRequest.of(0, (amount != null) ? amount : 10);
        return ResponseEntity.ok(advertService.getMostPopularAdverts(pageable));
    }

    // --> A05 - Kullanıcıya ait ilanları sayfalandırılmış şekilde döndürür
    @GetMapping("/auth")
    @PreAuthorize("hasAnyAuthority('CUSTOMER')")
    public Page<AdvertResponse> getAllAdvertForAuthUserByPage(HttpServletRequest request,
                                                              @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                              @RequestParam(value = "size", required = false, defaultValue = "20") int size,
                                                              @RequestParam(value = "sort", required = false, defaultValue = "category.id") String sort,
                                                              @RequestParam(value = "type", required = false, defaultValue = "asc") String type) {

        return advertService.getAllAdvertForAuthUser(request, page, size, sort, type);
    }

    // --> A06 - Yönetici ve yöneticiler için ilanları belirli kriterlere göre filtreleyip sayfalı ve sıralı bir şekilde döndürür.
    @GetMapping("/adverts/admin")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseEntity<Page<AdvertResponse>> getAdverts(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "category_id", required = false) Long categoryId,
            @RequestParam(value = "advert_type_id", required = false) Long advertTypeId,
            @RequestParam(value = "price_start", required = false) Double priceStart,
            @RequestParam(value = "price_end", required = false) Double priceEnd,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @RequestParam(value = "sort", defaultValue = "category_id") String sort,
            @RequestParam(value = "type", defaultValue = "asc") String type) {
        return ResponseEntity.ok(advertService.getFilteredAdverts(query, categoryId, advertTypeId, priceStart, priceEnd, status, page, size, sort, type));
    }

    // --> A07 - Belirtilen slug değerine sahip bir ilanı getirir.
    @GetMapping("/{slug}")
    @PreAuthorize("permitAll()") // http://localhost:8080/adverts/lux-villa-in-river-park
    public ResponseEntity<AdvertDetailsForSlugResonse> getAdvertBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(advertService.getAdvertBySlug(slug));
    }

    // --> A08
    @GetMapping("/{id}/auth")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<AdvertResponse> getAuthenticatedUserAdvert(@PathVariable Long id, HttpServletRequest request) {
        return ResponseEntity.ok(advertService.getAdvertByIdAndAuthenticatedUser(id, request));
    }

    // --> A09
    @GetMapping("/{id}/admin")// http://localhost:8080/2/admin
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseEntity<AdvertResponse> getAdvertById(@PathVariable Long id, HttpServletRequest request) {
        return ResponseEntity.ok(advertService.getAdvertById(id, request));
    }//A09

    // --> A10
    @PostMapping("/save") // Yeni bir ilan oluşturur.
    @PreAuthorize("hasRole('CUSTOMER')") // http://localhost:8080/adverts/save
    public ResponseEntity<AdvertResponse> createAdvert(
            @RequestBody AdvertRequest advertRequest,
            HttpServletRequest request, @RequestPart("files") MultipartFile[] files) {
        return ResponseEntity.ok(advertService.createAdvert(advertRequest, request, files));
    }

    // --> A11
    @PostMapping("/auth/{id}") // http://localhost:8080/adverts/auth/23
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseMessage<AdvertResponse> updateUsersAdvertById(
            @RequestParam @Valid AdvertRequest advertRequest,
            @RequestParam MultipartFile[] files,
            HttpServletRequest httpServletRequest, @PathVariable Long id) {
        return advertService.updateAuthenticatedAdvert(advertRequest, files, httpServletRequest, id);
    }

    // --> A12
    @PutMapping("/admin/{id}") // İlan güncelleme işlemi
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')") // http://localhost:8080/adverts/admin/23
    public ResponseMessage<AdvertResponse> updateAdvertById(
            @RequestBody @Valid AdvertRequest advertRequest,
            @RequestPart("files") MultipartFile[] files,
            HttpServletRequest httpServletRequest, @PathVariable Long id) {
        return advertService.updateAdvert(advertRequest, files, httpServletRequest, id);
    }

    // --> A13
    @DeleteMapping("/admin/{id}")  // http://localhost:8080/adverts/admin/5
    @PreAuthorize("hasAnyAuthority('MANAGER', 'ADMIN')")
    public ResponseEntity<AdvertResponse> deleteAdvertById(@PathVariable Long id, HttpServletRequest request) {
        return ResponseEntity.ok(advertService.deleteAdvert(id, request));
    }
}