package com.project.service.business;

import com.project.entity.concretes.business.City;
import com.project.entity.concretes.business.Country;
import com.project.exception.ResourceNotFoundException;
import com.project.payload.response.business.CityResponse;
import com.project.repository.business.CityRepository;
import com.project.repository.business.CountryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;


@Service
@RequiredArgsConstructor
public class CityService {


    private final CityRepository cityRepository;
    private final CountryRepository countryRepository;
    private final RestTemplate restTemplate; // API çağrıları için RestTemplate
    private static final String CITY_API_URL = "https://wft-geo-db.p.rapidapi.com/v1/geo/countries/{countryCode}/cities"; // Örnek GeoDB Cities API endpoint'i


    public List<City> getAllCities() {
        return cityRepository.findAll();
    }

    // Yeni metod
    public City getCityById(Long id) {
        return cityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("City not found with id: " + id));
    }


    // Bir ülkeye ait şehirleri API'den yükleyen metod
    public void loadCitiesFromApi(String countryCode) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-RapidAPI-Key", "YOUR_API_KEY");
        headers.set("X-RapidAPI-Host", "wft-geo-db.p.rapidapi.com");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        String url = CITY_API_URL.replace("{countryCode}", countryCode);
        ResponseEntity<CityResponse[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, CityResponse[].class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            Country country = countryRepository.findByIsoCode(countryCode)
                    .orElseThrow(() -> new ResourceNotFoundException("Country not found with ISO code: " + countryCode));

            for (CityResponse cityResponse : response.getBody()) {
                City city = City.builder()
                        .name(cityResponse.getName()) // Şehir ismi
                        .country(country) // İlgili ülkeyi set et
                        .build();
                cityRepository.save(city); // Şehri veritabanına kaydet
            }
        } else {
            throw new RuntimeException("Failed to fetch cities from API");
        }
    }
}

