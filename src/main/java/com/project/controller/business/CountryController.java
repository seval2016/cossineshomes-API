package com.project.controller.business;


import com.project.payload.response.business.CountryResponse;

import com.project.service.business.CountryService;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/countries")
@RequiredArgsConstructor
public class CountryController {

    private final CountryService countryService;

    @GetMapping
    @PreAuthorize("permitAll") // ANONYMOUS
    public List<CountryResponse> getCountries() {
        return countryService.getAllCountries().stream()
                .map(country -> {
                    CountryResponse response = new CountryResponse();
                    response.setId(country.getId());
                    response.setName(country.getName());
                    return response;
                })
                .collect(Collectors.toList());
    }
}