package com.project.service.business;

import com.project.entity.concretes.business.City;
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

    public ResponseEntity<List<City>> getCityById(Long countryId) {

        // Belirtilen countryId'ye ait şehirleri getir
        List<City> cities = cityRepository.findByCountryId(countryId);

        // Eğer şehirler bulunamazsa 404 döndür
        if (cities.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null); // Boş body ile 404 döndür
        }
        // Şehirler bulunduysa 200 OK ile birlikte listeyi döndür
        return ResponseEntity.ok(cities);

    }




}
