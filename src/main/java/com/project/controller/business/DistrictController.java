package com.project.controller.business;


import com.project.entity.concretes.business.District;
import com.project.payload.response.business.DistrictResponse;
import com.project.payload.response.business.ResponseMessage;
import com.project.service.business.DistrictService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/districts")
@RequiredArgsConstructor
public class DistrictController {

    private final DistrictService districtService;

    @GetMapping("/districts")
    @PreAuthorize("permitAll()")//http://localhost:8080/districts
    public List<DistrictResponse> getAllDistrict(){
        return districtService.getAllDistrict();
    }

    @GetMapping("/getByDistrict/{cityId}") //http://localhost:8080/districts/getByDistrict/1
    public ResponseMessage<List<District>> getByDistrict(@PathVariable Long cityId) {
        return districtService.getByDistrict(cityId);
    }
}