package com.project.service.business;

import com.project.entity.concretes.business.Country;
import com.project.exception.ResourceNotFoundException;
import com.project.payload.response.business.CountryResponse;

import com.project.repository.business.CountryRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;


@Service
@RequiredArgsConstructor
public class CountryService {

    private final CountryRepository countryRepository;
    private final RestTemplate restTemplate; // API çağrıları için Spring RestTemplate kullanıyoruz

    private static final String COUNTRY_API_URL = "https://restcountries.com/v3.1/all";

    public List<Country> getAllCountries() {
        return countryRepository.findAll();
    }

    public Country getCountryById(Long id) {
        return countryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Country not found with id: " + id));
    }

    // API ile tüm ülkeleri otomatik yükleyen metod
    public void loadCountriesFromApi() {
        ResponseEntity<CountryResponse[]> response = restTemplate.getForEntity(COUNTRY_API_URL, CountryResponse[].class);
        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            for (CountryResponse countryResponse : response.getBody()) {
                Country country = Country.builder()
                        .name(countryResponse.getName()) // Ülkenin ismi
                        .build();
                countryRepository.save(country); // Ülkeyi veritabanına kaydet
            }
        } else {
            throw new RuntimeException("Failed to fetch countries from API");
        }
    }
}
