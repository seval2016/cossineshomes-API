package com.project.service.business;

import com.project.entity.concretes.business.City;
import com.project.exception.ResourceNotFoundException;
import com.project.payload.messages.ErrorMessages;
import com.project.payload.messages.SuccessMessages;
import com.project.payload.response.ResponseMessage;
import com.project.payload.response.business.AdvertResponse;
import com.project.payload.response.business.CityResponse;
import com.project.repository.business.CityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CityService {


    private final CityRepository cityRepository;

    public List<City> getAllCity() {
        return cityRepository.findAll();
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
