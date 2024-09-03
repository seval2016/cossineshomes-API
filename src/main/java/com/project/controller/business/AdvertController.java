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