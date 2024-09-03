package com.project.controller.business;

import com.project.payload.request.business.AdvertRequest;
import com.project.payload.response.business.AdvertResponse;

import com.project.payload.response.business.CityAdvertResponse;
import com.project.service.business.AdvertService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;


import java.math.BigDecimal;
import java.util.List;


@RestController
@RequestMapping("/adverts")
@RequiredArgsConstructor
public class AdvertController {

    private final AdvertService advertService;

    @GetMapping //Belirli filtreleme kriterlerine göre ilanları getirir.
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

    @PostMapping //Yeni bir ilan oluşturur.
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<AdvertResponse> createAdvert(@RequestBody AdvertRequest advertRequest) {
        AdvertResponse advertResponse = advertService.createAdvert(advertRequest);
        return new ResponseEntity<>(advertResponse, HttpStatus.CREATED);
    }//A10

    // Yeni endpoint
    @GetMapping("/cities")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<CityAdvertResponse>> getAdvertCities() {
        List<CityAdvertResponse> cityAdverts = advertService.getAdvertCities();
        return ResponseEntity.ok(cityAdverts);
    }//A02
}
/*
Aşağıdaki  şartlara ve entitylere göre controller, AdvertService,AdvertRequest,AdvertResponse, AdvertRepository,AdvertMapper gibi gerekli olan tüm classları açıklayarak yazar mısın

@PostMapping
@PreAuthorize -> ANONYMOUS

/adverts/cities

Payload
()

Response
( Array<citydto> )
 [
    {
        “city": “İstanbul”,
        “amount": 453,
    },
    {
        “city": “Ankara”,
        “amount": 352,
    }
 ]

Requirements
– The adverts should be grouped by
cities

 */