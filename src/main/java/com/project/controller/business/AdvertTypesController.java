package com.project.controller.business;

import com.project.payload.request.business.AdvertTypesRequest;
import com.project.payload.response.business.advert.AdvertTypesResponse;
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
    public List<AdvertTypesResponse> getAllAdvertTypes(){
        return advertTypesService.getAllAdvertTypes();
    }

    // --> T02
    @GetMapping("/advert-types/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public AdvertTypesResponse getAdvertTypeById(@PathVariable Long id){
        return advertTypesService.getAdvertTypeById(id);
    }

    // --> T03
    @PostMapping("/advert-types")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseMessage<AdvertTypesResponse> createAdvertType(@Valid @RequestBody AdvertTypesRequest advertTypesRequest){
        return advertTypesService.saveAdvertType(advertTypesRequest);
    }

    // --> T04
    @PutMapping("/advert-types/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseEntity<AdvertTypesResponse> updateAdvertTypeById(@PathVariable Long id, @RequestBody AdvertTypesRequest advertTypesRequest){
        return ResponseEntity.ok(advertTypesService.updateAdvertTypeById(id, advertTypesRequest));
    }

    // --> T05
    @DeleteMapping("/advert-types/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseMessage deleteAdvertType(@PathVariable Long id){
        return advertTypesService.deleteAdvertTypeById(id);
    }
}
