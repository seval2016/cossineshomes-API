package com.project.controller.business;

import com.project.payload.request.business.AdvertRequest;
import com.project.payload.response.business.AdvertResponse;
import com.project.payload.response.business.CategoryAdvertResponse;
import com.project.payload.response.business.CityAdvertResponse;
import com.project.payload.response.business.ResponseMessage;
import com.project.service.business.AdvertService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;


@RestController
@RequestMapping("/adverts")
@RequiredArgsConstructor
public class AdvertController {

    private final AdvertService advertService;

    @GetMapping //Belirli filtreleme kriterlerine göre ilanları getirir.
    @PreAuthorize("permitAll()")
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

    @PostMapping("/save") // Yeni bir ilan oluşturur.
    @PreAuthorize("hasRole('CUSTOMER')") // http://localhost:8080/adverts/save
    public ResponseEntity<AdvertResponse> createAdvert(@RequestBody AdvertRequest advertRequest, HttpServletRequest request) {
        AdvertResponse advertResponse = advertService.createAdvert(advertRequest,request);
        return new ResponseEntity<>(advertResponse, HttpStatus.CREATED);
    }//A10

    // Yeni endpoint
    @GetMapping("/cities")
    @PreAuthorize("permitAll()") // http://localhost:8080/adverts/cities
    public ResponseEntity<List<CityAdvertResponse>> getAdvertCities() {
        List<CityAdvertResponse> cityAdverts = advertService.getAdvertCities();
        return ResponseEntity.ok(cityAdverts);
    }//A02

    @GetMapping("/categories")
    @PreAuthorize("permitAll()") // http://localhost:8080/adverts/types
    public ResponseEntity<List<CategoryAdvertResponse>> getAdvertCategories() {
        List<CategoryAdvertResponse> categoryAdverts = advertService.getAdvertCategories();
        return ResponseEntity.ok(categoryAdverts);
    }//A03

    // Yeni endpoint: authenticated user's advert update
    @PostMapping("/auth/{id}") // http://localhost:8080/adverts/auth/23
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseMessage<AdvertResponse> updateAuthenticatedAdvert(@PathVariable Long id, @RequestBody AdvertRequest advertRequest,
                                                        HttpServletRequest request) {

        return advertService.updateAuthenticatedAdvert(id, advertRequest, request);
    } //A11

    @PutMapping("/admin/{id}") // İlan güncelleme işlemi
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')") // http://localhost:8080/adverts/admin/23
    public ResponseMessage<AdvertResponse> updateAdminById(@RequestBody @Valid AdvertRequest advertRequest, @PathVariable Long id ) {
        return advertService.updateAdvert(id, advertRequest);
    } //A12

    @DeleteMapping("/admin/{id}")  // http://localhost:8080/adverts/admin/5
    @PreAuthorize("hasAnyAuthority('MANAGER', 'ADMIN')")
    public ResponseEntity<AdvertResponse> deleteAdvertById(@PathVariable Long id,HttpServletRequest request) {
        AdvertResponse response = advertService.deleteAdvert(id,request);
        return ResponseEntity.ok(response);
    } //A13
}