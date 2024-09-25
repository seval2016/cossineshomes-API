package com.project.service.business;


import com.project.entity.concretes.business.City;
import com.project.entity.concretes.business.District;
import com.project.exception.ResourceNotFoundException;

import com.project.payload.response.business.DistrictResponse;

import com.project.repository.business.CityRepository;
import com.project.repository.business.DistrictRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import java.util.List;


@Service
@RequiredArgsConstructor
public class DistrictService {

    private final DistrictRepository districtRepository;
    private final CityRepository cityRepository;
    private final RestTemplate restTemplate;
    private static final String DISTRICT_API_URL = "https://wft-geo-db.p.rapidapi.com/v1/geo/cities/{cityId}/districts";

    public List<District> getAllDistricts() {
        return districtRepository.findAll();
    }

    // Yeni metod
    public District getDistrictById(Long id) {
        return districtRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("District not found with id: " + id));
    }

    // Bir şehre ait ilçeleri API'den yükleyen metod
    public void loadDistrictsFromApi(Long cityId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-RapidAPI-Key", "YOUR_API_KEY");
        headers.set("X-RapidAPI-Host", "wft-geo-db.p.rapidapi.com");

        HttpEntity<String> entity = new HttpEntity<>(headers);
        String url = DISTRICT_API_URL.replace("{cityId}", String.valueOf(cityId));

        ResponseEntity<DistrictResponse[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, DistrictResponse[].class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            City city = cityRepository.findById(cityId)
                    .orElseThrow(() -> new ResourceNotFoundException("City not found with id: " + cityId));

            for (DistrictResponse districtResponse : response.getBody()) {
                District district = District.builder()
                        .name(districtResponse.getName()) // İlçe ismi
                        .city(city) // İlgili şehri set et
                        .build();
                districtRepository.save(district); // İlçeyi veritabanına kaydet
            }
        } else {
            throw new RuntimeException("Failed to fetch districts from API");
        }
    }
}