package com.project.controller.business;


import com.project.payload.response.business.DistrictResponse;
import com.project.service.business.DistrictService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/districts")
@RequiredArgsConstructor
public class DistrictController {

    private DistrictService districtService;

    @GetMapping
    public List<DistrictResponse> getAllDistricts() {
        return districtService.getAllDistricts();
    }
}