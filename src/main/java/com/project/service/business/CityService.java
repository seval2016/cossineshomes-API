package com.project.service.business;

import com.project.entity.concretes.business.City;
import com.project.exception.ResourceNotFoundException;
import com.project.payload.messages.ErrorMessages;
import com.project.payload.response.business.ResponseMessage;
import com.project.repository.business.CityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CityService {


    private final CityRepository cityRepository;

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

        Long cityId = 1L;

        City city = cityRepository.findById(cityId).orElseThrow(() -> new RuntimeException(ErrorMessages.CITY_NOT_FOUND));
        cityRepository.save(city);
    }
}
