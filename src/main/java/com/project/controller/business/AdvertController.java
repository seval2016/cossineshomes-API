package com.project.controller.business;

import com.project.entity.concretes.business.Advert;
import com.project.payload.mappers.AdvertMapper;
import com.project.payload.response.abstracts.BaseUserResponse;
import com.project.payload.response.business.AdvertResponse;
import com.project.payload.response.business.CategoryAdvertResponse;
import com.project.payload.response.business.CityAdvertResponse;
import com.project.repository.business.AdvertRepository;
import com.project.service.business.AdvertService;
import com.project.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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
        List<CityAdvertResponse> cityAdverts = advertService.getAdvertsGroupedByCities();
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
    public ResponseEntity<Page<AdvertResponse>> getAuthenticatedUserAdverts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "category_id") String sortField,
            @RequestParam(defaultValue = "asc") String sortType,
            HttpServletRequest request) {

        // Get the authenticated user from the request
        BaseUserResponse currentUser = userService.getAuthenticatedUser(request);

        // Set sorting and pagination
        Sort sort = Sort.by(Sort.Direction.fromString(sortType), sortField);
        Pageable pageable = PageRequest.of(page, size, sort);

        // Fetch adverts for the authenticated user
        Page<Advert> adverts = advertRepository.findByUser(currentUser, pageable);

        // Map the adverts to AdvertResponse
        Page<AdvertResponse> advertResponses = adverts.map(advertMapper::mapAdvertToAdvertResponse);

        return ResponseEntity.ok(advertResponses);
    }//A05

    /*@PostMapping("/save") // Yeni bir ilan oluşturur.
    @PreAuthorize("hasRole('CUSTOMER')") // http://localhost:8080/adverts/save
    public ResponseEntity<AdvertResponse> createAdvert(@RequestBody AdvertRequest advertRequest, HttpServletRequest request) {
        AdvertResponse advertResponse = advertService.createAdvert(advertRequest,request);
        return new ResponseEntity<>(advertResponse, HttpStatus.CREATED);
    }//A10

    // Yeni endpoint: authenticated user's advert update
    @PostMapping("/auth/{id}") // http://localhost:8080/adverts/auth/23
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseMessage<AdvertResponse> updateAuthenticatedAdvert(@PathVariable Long id, @RequestBody AdvertRequest advertRequest,
                                                        HttpServletRequest request) {

        return advertService.updateAuthenticatedAdvert(id, advertRequest, request);
    } //A11

   /* @PutMapping("/admin/{id}") // İlan güncelleme işlemi
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')") // http://localhost:8080/adverts/admin/23
    public ResponseMessage<AdvertResponse> updateAdvertById(@RequestBody @Valid AdvertRequest advertRequest,
                                                            @RequestPart("files") MultipartFile[] files,
                                                            @PathVariable Long id,
                                                            HttpServletRequest httpServletRequest) {
        return advertService.updateAdvert(advertRequest,id,httpServletRequest,files);
    } //A12

    @DeleteMapping("/admin/{id}")  // http://localhost:8080/adverts/admin/5
    @PreAuthorize("hasAnyAuthority('MANAGER', 'ADMIN')")
    public ResponseEntity<AdvertResponse> deleteAdvertById(@PathVariable Long id,HttpServletRequest request) {
        AdvertResponse response = advertService.deleteAdvert(id,request);
        return ResponseEntity.ok(response);
    } //A13*/
}