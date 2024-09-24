package com.project.service.business;

import com.project.entity.concretes.business.City;
import com.project.entity.concretes.business.Country;
import com.project.exception.ResourceNotFoundException;
import com.project.payload.messages.ErrorMessages;
import com.project.payload.request.business.CityRequest;
import com.project.payload.response.business.ResponseMessage;
import com.project.repository.business.CityRepository;
import com.project.repository.business.CountryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CityService {


    private final CityRepository cityRepository;
    private final CountryRepository countryRepository;

    public ResponseMessage<List<City>> getAllCity() {

        List<City> cityList=cityRepository.findAll();

        return ResponseMessage.<List<City>>builder()
                .httpStatus(HttpStatus.OK)
                .message("Cities were brought succesfully.")
                .object(cityList)
                .build();
    }

    public City getCityById(Long countryId) {

        return cityRepository.findById(countryId).orElseThrow(()->new ResourceNotFoundException(ErrorMessages.CITY_NOT_FOUND));

    }

    public int countAllCities() {

        return cityRepository.countAllCities();
    }

    public void setBuiltInForCity() {
        CityRequest defaultCity = new CityRequest();
        defaultCity.setName("İstanbul");
        defaultCity.setCountry_id(1L);
        saveCity(defaultCity);
    }

    public City saveCity(CityRequest cityRequest) {
        // Country exists check
        Country country = countryRepository.findById(cityRequest.getCountry_id())
                .orElseThrow(() -> new RuntimeException("Country with ID " + cityRequest.getCountry_id() + " does not exist."));

        // Build the City entity from the request
        City city = City.builder()
                .name(cityRequest.getName())
                .country(country)
                .build();

        // Save the city entity to the database
        return cityRepository.save(city);
    }

    public City getCityByName(String cityName) {
       List<City> cities  = cityRepository.findByName(cityName);

        if (cities .isEmpty()) {
            throw new RuntimeException("City not found: " + cityName);
        }
        return cities.get(0);
    }
}
