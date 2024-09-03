package com.project.controller.business;


import com.project.payload.response.business.CountryResponse;
import com.project.service.business.CountryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/countries")
@RequiredArgsConstructor
public class CountryController {

    @Autowired
    private CountryService countryService;

    @PreAuthorize("permitAll()")
    @GetMapping
    public List<CountryResponse> getAllCountries() {
        return countryService.getAllCountries();
    }
}