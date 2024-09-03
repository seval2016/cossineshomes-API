package com.project.controller.business;

import com.project.payload.response.business.CityResponse;
import com.project.service.business.CityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/cities")
public class CityController {

    @Autowired
    private CityService cityService;

    @PreAuthorize("permitAll()")
    @GetMapping
    public List<CityResponse> getAllCities() {
        return cityService.getAllCities();
    }
}
