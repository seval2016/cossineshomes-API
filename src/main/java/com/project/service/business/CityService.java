package com.project.service.business;

import com.project.entity.concretes.business.City;
import com.project.payload.response.business.CityResponse;
import com.project.entity.concretes.business.business.CityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CityService {

    @Autowired
    private CityRepository cityRepository;

    public List<CityResponse> getAllCities() {
        List<City> cities = cityRepository.findAll();
        return cities.stream().map(city -> {
            CityResponse dto = new CityResponse();
            dto.setId(city.getId());
            dto.setName(city.getName());
            dto.setCountryId(city.getCountry().getId());
            return dto;
        }).collect(Collectors.toList());
    }
}
