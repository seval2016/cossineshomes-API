package com.project.controller.business;

import com.project.payload.request.business.AdvertRequest;
import com.project.payload.response.business.*;
import com.project.payload.response.business.advert.*;
import com.project.payload.response.business.category.CategoryAdvertResponse;
import com.project.service.business.AdvertService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    /**
     * A01 - /adverts
     * Belirli filtreleme kriterlerine göre ilanları getirir.
     * http://localhost:8080/adverts?q=beyoglu&category_id=12&advert_type_id=3&price_start=500&price_end=1500 location=34&status=1;page=1&size=10&sort=date&type=asc
     *
     * @return List of AdvertListResponse
     */
    @GetMapping
    public ResponseEntity<Page<AdvertListResponse>> getAdvertsByPage(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long advertTypeId,
            @RequestParam(required = false) BigDecimal priceStart,
            @RequestParam(required = false) BigDecimal priceEnd,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "category_id") String sort,
            @RequestParam(defaultValue = "asc") String type) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(type), sort));
        return ResponseEntity.ok(advertService.getAdvertsByPage(q, categoryId, advertTypeId, priceStart, priceEnd, status, pageable));
    }


    /**
     * A02 - /adverts/cities
     * İlanlar şehre göre gruplanarak sadece aktif ilanların sayısı döndürülür.
     * http://localhost:8080/adverts/cities
     * @return List of CityAdvertResponse
     */

    @GetMapping("/cities")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<CityAdvertResponse>> getAdvertsGroupedByCities() {
        return ResponseEntity.ok(advertService.getAdvertsGroupedByCities());
    }

    /**
     * A03 - /adverts/categories
     * Kategori ismini ve o kategoriye ait reklam sayısını döndürme..
     * http://localhost:8080/adverts/categories
     * @return List of CategoryAdvertResponse
     */

    @GetMapping("/categories")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<CategoryAdvertResponse>> getAdvertsGroupedByCategory() {
        return ResponseEntity.ok(advertService.getAdvertsGroupedByCategory());
    }

    /**
     * A04 - /adverts/popular
     * En popüler ilanları döner, miktar parametresi opsiyonel (default: 10)
     * http://localhost:8080/adverts/popular/20
     * @param amount Limit sayısı (default: 10)
     * @return List of PopularAdvertResponse
     */
    @GetMapping("/popular")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<PopularAdvertResponse>> getPopularAdvertsDefault(@RequestParam(required = false) Integer amount) {
        List<PopularAdvertResponse> popularAdverts = advertService.getPopularAdverts(amount);
        return ResponseEntity.ok(popularAdverts);
    }

    /**
     * A05 - /adverts/auth
     * Kullanıcıya ait ilanları sayfalandırılmış şekilde döndürür
     * http://localhost:8080/adverts/auth/?page=1&size=10&sort=date&type=asc
     * @return List of AdvertResponseForCustomer
     */

    @GetMapping("/auth")
    @PreAuthorize("hasAnyAuthority('CUSTOMER')")
    public Page<AdvertResponseForCustomer> getAllAdvertForAuthUserByPage(HttpServletRequest request,
                                                              @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                              @RequestParam(value = "size", required = false, defaultValue = "20") int size,
                                                              @RequestParam(value = "sort", required = false, defaultValue = "category_id") String sort,
                                                              @RequestParam(value = "type", required = false, defaultValue = "asc") String type) {

        return advertService.getAllAdvertForAuthUser(request, page, size, sort, type);
    }

    /**
     * A06 - /adverts/admin
     * Yönetici ve yöneticiler için ilanları belirli kriterlere göre filtreleyip sayfalı ve sıralı bir şekilde döndürür.
     * http://localhost:8080/adverts/admin/?q=beyoğlu&category_id=12&advert_type_id=3&price_start=500&price_end=1500&status=1;page
     * =1&size=10&sort=date&type=asc
     * @return List of AdvertResponse
     */

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

    /**
     * A07 - /adverts/:slug
     * Belirtilen slug değerine sahip bir ilanı getirir.
     * http://localhost:8080/adverts/lux-villa-in-river-park
     * @return  AdvertDetailsForSlugResponse
     */

    @GetMapping("/{slug}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<AdvertDetailsForSlugResponse> getAdvertBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(advertService.getAdvertBySlug(slug));
    }

    /**
     * A08 - /adverts/:id/auth
     * Kullanıcının authenticated olduğu ilanın ID'sine göre bulur.
     * http://localhost:8080/2/auth
     * @return AdvertResponseForUser
     */

    @GetMapping("/{id}/auth")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<AdvertResponseForUser> getAdvertByIdForCustomer(@PathVariable Long id, HttpServletRequest request) {
        return ResponseEntity.ok(advertService.getAdvertByIdForCustomer(id, request));
    }

    /**
     * A09 - /adverts/:id/admin
     * Belirtilen ID'ye göre ilanı getirir; yalnızca MANAGER veya ADMIN rolüne sahip kullanıcılar tarafından erişilebilir.
     * http://localhost:8080/2/admin
     * @return AdvertResponseForUser
     */
    @GetMapping("/{id}/admin")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseEntity<AdvertResponseForUser> getAdvertById(@PathVariable Long id, HttpServletRequest request) {
        return ResponseEntity.ok(advertService.getAdvertByIdForAdmin(id, request));
    }

    /**
     * A10 - /adverts
     * Yeni bir ilan oluşturur; yalnızca CUSTOMER rolüne sahip kullanıcılar tarafından erişilebilir.
     * http://localhost:8080/adverts/save
     * @return AdvertResponse
     */
    @PostMapping("/save")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<AdvertResponse> createAdvert(
            @RequestBody AdvertRequest advertRequest,
            HttpServletRequest request, @RequestPart("files") MultipartFile[] files) {
        return ResponseEntity.ok(advertService.createAdvert(advertRequest, request, files));
    }

    /**
     * A11 - /adverts/auth/:id
     * Id'si verilen (kimliği doğrulanmış kullanıcıların) kendi reklamlarını güncellemelerine olanak tanır.
     * http://localhost:8080/adverts/auth/23
     * @return AdvertResponse
     */
    @PostMapping("/auth/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseMessage<AdvertResponse> updateUsersAdvertById(
            @RequestParam @Valid AdvertRequest advertRequest,
            @RequestParam MultipartFile[] files,
            HttpServletRequest httpServletRequest, @PathVariable Long id) {
        return advertService.updateAuthenticatedAdvert(advertRequest, files, httpServletRequest, id);
    }
    /**
     * A12 - /adverts/admin/:id
     * Id'si verilen admin ve admin manager tüm ilanları güncelleyebilmesine olanak tanır. kendi reklamlarını güncellemelerine olanak tanır.
     * http://localhost:8080/adverts/admin/23
     * @return AdvertResponse
     */
    @PutMapping("/admin/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseMessage<AdvertResponse> updateAdvertById(
            @RequestBody @Valid AdvertRequest advertRequest,
            @RequestPart("files") MultipartFile[] files,
            HttpServletRequest httpServletRequest, @PathVariable Long id) {
        return advertService.updateAdvert(advertRequest, files, httpServletRequest, id);
    }
    /**
     * A13 - /adverts/admin/:id
     * id'si verilen advert'ın silinmesi
     * http://localhost:8080/adverts/admin/5
     * @return AdvertResponse
     */

    @DeleteMapping("/admin/{id}")  //
    @PreAuthorize("hasAnyAuthority('MANAGER', 'ADMIN')")
    public ResponseEntity<AdvertResponse> deleteAdvertById(@PathVariable Long id, HttpServletRequest request) {
        return ResponseEntity.ok(advertService.deleteAdvert(id, request));
    }
}