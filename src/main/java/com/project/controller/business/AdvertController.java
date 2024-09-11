package com.project.controller.business;

import com.project.payload.mappers.AdvertMapper;
import com.project.payload.request.business.AdvertRequest;
import com.project.payload.response.business.AdvertResponse;
import com.project.payload.response.business.CategoryAdvertResponse;
import com.project.payload.response.business.CityAdvertResponse;
import com.project.payload.response.business.ResponseMessage;
import com.project.repository.business.AdvertRepository;
import com.project.service.business.AdvertService;
import com.project.service.helper.MethodHelper;
import com.project.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
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
    private final UserService userService;
    private final AdvertRepository advertRepository;
    private final AdvertMapper advertMapper;
    private final MethodHelper methodHelper;

    //Belirli filtreleme kriterlerine göre ilanları getirir.
    @GetMapping //http://localhost:8080/adverts?q=beyoğlu&category_id=12&advert_type_id=3&price_start=500&price_end=1500 location=34 & status=1;page=1&size=10&sort=date&type=asc
    public ResponseEntity<Page<AdvertResponse>> getAdverts(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long advertTypeId,
            @RequestParam(required = false) BigDecimal priceStart,
            @RequestParam(required = false) BigDecimal priceEnd,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "categoryId") String sort,
            @RequestParam(defaultValue = "asc") String type) {

        Page<AdvertResponse> adverts = advertService.getAdverts(q, categoryId, advertTypeId, priceStart, priceEnd, status, page, size, sort, type);
        return ResponseEntity.ok(adverts);
    }//A01

    @GetMapping("/cities")
    @PreAuthorize("permitAll()") // http://localhost:8080/adverts/cities
    public ResponseEntity<List<CityAdvertResponse>> getAdvertsGroupedByCities() {
        List<CityAdvertResponse> cityAdverts = methodHelper.getAdvertsGroupedByCities();
        return ResponseEntity.ok(cityAdverts);
    }//A02

    @GetMapping("/categories")
    @PreAuthorize("permitAll()") //http://localhost:8080/adverts/categories
    public ResponseEntity<List<CategoryAdvertResponse>> getAdvertsGroupedByCategory() {
        List<CategoryAdvertResponse> response = advertService.getAdvertsGroupedByCategory();
        return ResponseEntity.ok(response);
    }//A03

    @GetMapping("/popular/{amount}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<AdvertResponse>> getMostPopularAdverts(
            @PathVariable(value = "amount", required = false) Integer amount) {
        // amount null ise varsayılan değeri 10 olarak ayarla
        int limit = (amount != null) ? amount : 10;
        List<AdvertResponse> response = advertService.getMostPopularAdverts(limit);
        return ResponseEntity.ok(response);
    }//A04

    @GetMapping("/auth")
    @PreAuthorize("hasAnyAuthority('CUSTOMER')")
    public Page<AdvertResponse> getAllAdvertForAuthUserByPage(
            HttpServletRequest request,
            @RequestParam(value = "page",required = false,defaultValue = "0") int page,
            @RequestParam(value = "size",required = false, defaultValue = "20") int size,
            @RequestParam(value = "sort",required = false,defaultValue = "category.id") String sort,
            @RequestParam(value = "type",required = false,defaultValue = "asc") String type){

        return advertService.getAllAdvertForAuthUser(request,page,size,sort,type);
    }//A05

        @GetMapping("/adverts/admin")
        @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
        public ResponseEntity<Page<AdvertResponse>> getAdverts(
                @RequestParam(value = "q", required = false) String q,
                @RequestParam(value = "category_id", required = false) Long categoryId,
                @RequestParam(value = "advert_type_id", required = false) Long advertTypeId,
                @RequestParam(value = "price_start", required = false) Double priceStart,
                @RequestParam(value = "price_end", required = false) Double priceEnd,
                @RequestParam(value = "status", required = false) Integer status,
                @RequestParam(value = "page", defaultValue = "0") int page,
                @RequestParam(value = "size", defaultValue = "20") int size,
                @RequestParam(value = "sort", defaultValue = "category_id") String sort,
                @RequestParam(value = "type", defaultValue = "asc") String type) {

            Page<AdvertResponse> adverts = advertService.getFilteredAdverts(q, categoryId, advertTypeId, priceStart, priceEnd, status, page, size, sort, type);
            return ResponseEntity.ok(adverts);
        }//A06

    @GetMapping("/{slug}")
    @PreAuthorize("permitAll()") //http://localhost:8080/adverts/lux-villa-in-river-park
    public ResponseEntity<AdvertResponse> getAdvertBySlug(@PathVariable String slug) {
        AdvertResponse advertResponse = advertService.getAdvertBySlug(slug);
        return ResponseEntity.ok(advertResponse);
    }//A07

    @GetMapping("/{id}/auth")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<AdvertResponse> getAuthenticatedUserAdvert(@PathVariable Long id, HttpServletRequest request) {
        AdvertResponse advertResponse = advertService.getAdvertByIdAndAuthenticatedUser(id, request);
        return ResponseEntity.ok(advertResponse);
    }//A08

    @GetMapping("/{id}/admin")// http://localhost:8080/2/auth
    @PreAuthorize("hasAnyAuthority('CUSTOMER')")
    public ResponseEntity<AdvertResponse> getAdvertById(@PathVariable Long id, HttpServletRequest request) {

        return advertService.getAdvertById(id, request);

    }//A09

   @PostMapping("/save") // Yeni bir ilan oluşturur.
    @PreAuthorize("hasRole('CUSTOMER')") // http://localhost:8080/adverts/save
    public ResponseEntity<AdvertResponse> createAdvert(@RequestBody AdvertRequest advertRequest, HttpServletRequest request) {
        AdvertResponse advertResponse = advertService.createAdvert(advertRequest,request);
        return new ResponseEntity<>(advertResponse, HttpStatus.CREATED);
    }//A10

     // Yeni endpoint: authenticated user's advert update
    @PostMapping("/auth/{id}") // http://localhost:8080/adverts/auth/23
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseMessage<AdvertResponse> updateUsersAdvertById(@RequestParam @Valid AdvertRequest advertRequest,
                                                                 @RequestParam MultipartFile[] files,
                                                                 HttpServletRequest httpServletRequest,@PathVariable Long id) {
        return advertService.updateAuthenticatedAdvert(advertRequest, files, httpServletRequest, id);
    }//A11

    @PutMapping("/admin/{id}") // İlan güncelleme işlemi
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')") // http://localhost:8080/adverts/admin/23
    public ResponseMessage<AdvertResponse> updateAdvertById(@RequestBody @Valid AdvertRequest advertRequest,
                                                            @RequestPart("files") MultipartFile[] files,
                                                            HttpServletRequest httpServletRequest,@PathVariable Long id) {
        return advertService.updateAdvert(advertRequest,files,httpServletRequest,id);
    } //A12

    @DeleteMapping("/admin/{id}")  // http://localhost:8080/adverts/admin/5
    @PreAuthorize("hasAnyAuthority('MANAGER', 'ADMIN')")
    public ResponseEntity<AdvertResponse> deleteAdvertById(@PathVariable Long id,HttpServletRequest request) {
        AdvertResponse response = advertService.deleteAdvert(id,request);
        return ResponseEntity.ok(response);
    } //A13
}