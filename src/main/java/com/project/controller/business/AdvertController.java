package com.project.controller.business;

import com.project.payload.request.business.AdvertRequest;
import com.project.payload.response.business.AdvertResponse;

import com.project.service.business.AdvertService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;


import java.math.BigDecimal;


@RestController
@RequestMapping("/adverts")
@RequiredArgsConstructor
public class AdvertController {

    private final AdvertService advertService;

    @GetMapping
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

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<AdvertResponse> createAdvert(@RequestBody AdvertRequest advertRequest) {
        AdvertResponse advertResponse = advertService.createAdvert(advertRequest);
        return new ResponseEntity<>(advertResponse, HttpStatus.CREATED);
    }//A10

/*
    @PostMapping("/save")  // http://localhost:8080/adverts/save + POST + JSON
    @PreAuthorize("hasAnyAuthority('CUSTOMER')")
    public ResponseMessage<AdvertResponse> saveAdvert(@RequestBody @Valid AdvertRequest advertRequest) {
        return advertService.saveAdvert(advertRequest);
    }

    @GetMapping("/id") // http://localhost:8080/adverts/1 + GET
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public AdvertResponse getAdvertById(@PathVariable Long id) {
        return advertService.getAdvertById(id);
    }

    @GetMapping("/getAll") // http://localhost:8080/adverts/getAll + GET
    @PreAuthorize("hasAnyAuthority('ADMIN','CUSTOMER','MANAGER')")
    public List<AdvertResponse> getAllAdverts() {
        return advertService.getAllAdverts();
    }

    @GetMapping("/getAllAdvertsByPage") // http://localhost:8080/adverts/getAllAdvertsByPage + GET
    @PreAuthorize("hasAnyAuthority('ADMIN','CUSTOMER','MANAGER')")
    public Page<AdvertResponse> getAllAdvertsByPage(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "startDate") String sort,
            @RequestParam(value = "type", defaultValue = "desc") String type
    ) {

        return advertService.getAllAdvertsByPage(page, size, sort, type);

    }

    @DeleteMapping("/delete/{id}") // http://localhost:8080/adverts/delete/1 + DELETE
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseMessage<?> deleteAdvertById(@PathVariable Long id) {  // Burada ? bulunmasina gerek yok
        return advertService.deleteAdvertById(id);
    }

    @PutMapping("/update/{id}") // http://localhost:8080/adverts/update/1 + JSON +  PUT
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseMessage<AdvertResponse> updateAdvertById(@PathVariable Long id,
                                                            @RequestBody @Valid AdvertRequest advertRequest) {

        return advertService.updateAdvertById(id, advertRequest);
    }
*/
}
/*
Aşağıdaki  şartlara ve entitylere göre controller, AdvertService,AdvertRequest,AdvertResponse, AdvertRepository,AdvertMapper gibi gerekli olan tüm classları açıklayarak yazar mısın

@PostMapping
@PreAuthorize -> CUSTOMER
It should create an advert

Response (advert)
{ "id": 2, “title": "…", }

Requirements
- Default builtIn value is false
- Default create_at value is current
date and time
- It should return the advert that just
created

/adverts
Payload (body)
title district_id
desc category_id
price images
advert_type_id Properties=[ {
keyId: 12, value:
"4" }, { keyId: 44,
value: "12" }, {
keyId: 15, value:
"test" }, { keyId: 76,
value: "125" }]
country_id location
city_id

 */