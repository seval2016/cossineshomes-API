package com.project.controller.business;


import com.project.payload.response.business.DistrictResponse;
import com.project.service.business.DistrictService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/districts")
@RequiredArgsConstructor
public class DistrictController {

    private final DistrictService districtService;

    @GetMapping
    @PreAuthorize("permitAll") // ANONYMOUS
    public List<DistrictResponse> getDistricts() {
        return districtService.getAllDistricts().stream()
                .map(district -> {
                    DistrictResponse response = new DistrictResponse();
                    response.setId(district.getId());
                    response.setName(district.getName());
                    return response;
                })
                .collect(Collectors.toList());
    }
}