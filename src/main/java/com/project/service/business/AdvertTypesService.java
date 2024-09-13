package com.project.service.business;

import com.project.entity.concretes.business.AdvertType;
import com.project.payload.request.business.AdvertTypeRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Service
@RequiredArgsConstructor
public class AdvertTypesService {
    private final AdvertTypesService advertTypesService;

    @GetMapping
    public List<AdvertTypeResponse> getAllAdvertTypes(){
        return advertTypesService.getAllAdvertTypes();
    }

    @GetMapping("/advert-types/{id}")
    @PreAuthorize("hasAnyAuthority('MANAGER','ADMIN')")
    public AdvertTypeResponse getAdvertTypeById(@PathVariable Long id){
        return advertTypesService.getAdvertTypeById(id);
    }

    @PostMapping("/advert-types")
    @PreAuthorize("hasAnyAuthority('MANAGER','ADMIN')")
    public ResponseMessage<AdvertTypeResponse> createAdvertType(@Valid @RequestBody AdvertTypeRequest advertTypeRequest){
        return advertTypesService.saveAdvertType(advertTypeRequest);
    }

    @PutMapping("/advert-types/{id}")
    @PreAuthorize("hasAnyAuthority('MANAGER','ADMIN')")
    public ResponseEntity<AdvertTypeResponse> updateAdvertTypeById(@PathVariable Long id, @RequestBody AdvertTypeRequest advertTypeRequest){
        return ResponseEntity.ok(advertTypesService.updateAdvertTypeById(id, advertTypeRequest));
    }

    @DeleteMapping("/advert-types/{id}")
    @PreAuthorize("hasAnyAuthority('MANAGER','ADMIN')")
    public ResponseMessage deleteAdvertType(@PathVariable Long id){
        return advertTypesService.deleteAdvertTypeById(id);
    }
}
