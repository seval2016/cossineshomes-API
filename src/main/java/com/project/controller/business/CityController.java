package com.project.controller.business;
import com.project.payload.response.business.CityResponse;
import com.project.service.business.CityService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/cities")
@RequiredArgsConstructor
public class CityController {


    private final CityService cityService;

    @GetMapping
    @PreAuthorize("permitAll") // ANONYMOUS
    public List<CityResponse> getCities() {
        return cityService.getAllCities().stream()
                .map(city -> {
                    CityResponse response = new CityResponse();
                    response.setId(city.getId());
                    response.setName(city.getName());
                    return response;
                })
                .collect(Collectors.toList());
    }
}
