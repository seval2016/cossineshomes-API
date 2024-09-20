package com.project.controller.business;

import com.project.payload.request.business.AdvertTypeRequest;
import com.project.payload.response.business.advert.AdvertTypeResponse;
import com.project.payload.response.business.ResponseMessage;
import com.project.service.business.AdvertTypesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/advert-types")
@RequiredArgsConstructor
public class AdvertTypesController {

    private final AdvertTypesService advertTypesService;

    // --> T01
    @GetMapping
    public List<AdvertTypeResponse> getAllAdvertTypes(){
        return advertTypesService.getAllAdvertTypes();
    }

    // --> T02
    @GetMapping("/advert-types/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public AdvertTypeResponse getAdvertTypeById(@PathVariable Long id){
        return advertTypesService.getAdvertTypeById(id);
    }

    // --> T03
    @PostMapping("/advert-types")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseMessage<AdvertTypeResponse> createAdvertType(@Valid @RequestBody AdvertTypeRequest advertTypeRequest){
        return advertTypesService.saveAdvertType(advertTypeRequest);
    }

    // --> T04
    @PutMapping("/advert-types/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseEntity<AdvertTypeResponse> updateAdvertTypeById(@PathVariable Long id, @RequestBody AdvertTypeRequest advertTypeRequest){
        return ResponseEntity.ok(advertTypesService.updateAdvertTypeById(id, advertTypeRequest));
    }

    // --> T05
    @DeleteMapping("/advert-types/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseMessage deleteAdvertType(@PathVariable Long id){
        return advertTypesService.deleteAdvertTypeById(id);
    }
}
