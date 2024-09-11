package com.project.service.business;

import com.project.entity.concretes.business.Country;
import com.project.payload.response.business.CountryResponse;
import com.project.repository.business.CountryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CountryService {
    @Autowired
    private CountryRepository countryRepository;

    public List<CountryResponse> getAllCountries() {
        List<Country> countries = countryRepository.findAll();
        return countries.stream().map(country -> {
            CountryResponse dto = new CountryResponse();
            dto.setId(country.getId());
            dto.setName(country.getName());
            return dto;
        }).collect(Collectors.toList());
    }

    public Country getCountryById(Long countryId) {
        return null;
    }
}
