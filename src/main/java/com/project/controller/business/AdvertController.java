package com.project.controller.business;

import com.project.payload.request.business.AdvertRequest;
import com.project.payload.response.business.AdvertResponse;
import com.project.payload.response.business.CategoryAdvertResponse;
import com.project.payload.response.business.CityAdvertResponse;
import com.project.service.business.AdvertService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
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

    @PostMapping // Yeni bir ilan oluşturur.
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<AdvertResponse> createAdvert(@RequestBody AdvertRequest advertRequest, HttpServletRequest request) {
        AdvertResponse advertResponse = advertService.createAdvert(advertRequest,request);
        return new ResponseEntity<>(advertResponse, HttpStatus.CREATED);
    }//A10

    // Yeni endpoint
    @GetMapping("/cities")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<CityAdvertResponse>> getAdvertCities() {
        List<CityAdvertResponse> cityAdverts = advertService.getAdvertCities();
        return ResponseEntity.ok(cityAdverts);
    }//A02

    @GetMapping("/categories")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<CategoryAdvertResponse>> getAdvertCategories() {
        List<CategoryAdvertResponse> categoryAdverts = advertService.getAdvertCategories();
        return ResponseEntity.ok(categoryAdverts);
    }//A03

    // Yeni endpoint: authenticated user's advert update
    @PostMapping("/auth/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<AdvertResponse> updateAdvert(@PathVariable Long id, @RequestBody AdvertRequest advertRequest,
                                                       HttpServletRequest request) {
        AdvertResponse response = advertService.updateAdvert(id, advertRequest, request);
        return ResponseEntity.ok(response);
    } //A11

    @PostMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<AdvertResponse> deleteAdvert(@PathVariable Long id) {
        AdvertResponse deletedAdvert = advertService.deleteAdvert(id);
        return ResponseEntity.ok(deletedAdvert);
    } //A13
}
/*
Aşağıdaki  şartlara ve entitylere göre controller, AdvertService,AdvertRequest,AdvertResponse, AdvertRepository,AdvertMapper gibi gerekli olan tüm classları türkçe açıklayarak yazar mısın

security var token ile giriş yapılıyor

@PostMapping
@PreAuthorize -> MANAGER ,ADMIN

It should delete the user’s advert

/adverts/admin/:id

Payload
id: advertId (required)

Response
(product)
{ "id": 2, “title": "…", }

Requirements
- The advert whose builtIn property is
true can not be deleted.
- If any advert is deleted, related
records in tour_requests, favorites,
logs, images,
category,property_values also should
be deleted.

 */