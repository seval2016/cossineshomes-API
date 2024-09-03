package com.project.controller.business;

import com.project.payload.request.business.AdvertRequest;
import com.project.payload.response.business.AdvertResponse;
import com.project.payload.response.business.ResponseMessage;
import com.project.service.business.AdvertService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/adverts")
@RequiredArgsConstructor
public class AdvertController {

    private final AdvertService advertService;


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

}
/*
Aşağıdaki  şartlara ve entitylere göre controller service ve gerekli olan tüm classları açıklayarak yazar msıın
@GetMapping
@PreAuthorize -> ANONYMOUS
It should return adverts depending on query and paging parameters

/adverts?q=beyoğlu&category_id=12&advert_type_id=3&price_start=500&price_end=1500 location=34 &
status=1;page=1&size=10&sort=date&type=asc

Payload
(Queryvarchar)
q: search query (optional)
category_id: advert category
advert_type_id: advert type id
price_start: number (optional)
price_end: number (optional)
status: number(optional)
page: active page number (optional, default: 0)
size: record countin a page (optional, default: 20)
sort : sort field name (optional, default: category_id)
type: sorting type (optional, default: asc)


Response
( Array<advert> )
[
{ "id": 2,
“title": "…",
“image”: “…” …
}
]

Requirements
– Value of q parameter should be
searched in advert title and desc field
– Get the adverts whose active fields of
category and advert is 1, If q is omitted,
get all records according to the paging
params

 */