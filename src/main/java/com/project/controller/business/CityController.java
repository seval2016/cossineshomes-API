package com.project.controller.business;
import com.project.entity.concretes.business.City;
import com.project.payload.response.business.ResponseMessage;
import com.project.service.business.CityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cities")
@RequiredArgsConstructor
public class CityController {


    private final CityService cityService;

    @GetMapping("/cities") //http://localhost:8080/cities
    public ResponseMessage<List<City>> getAllCity(){
        return cityService.getAllCity();
    }

    @GetMapping("/getCityById/{id}") //http://localhost:8080/city/getByCity/1
    public ResponseEntity<List<City>> getCityById(@PathVariable Long countryId){
        return cityService.getCityById(countryId);
    }
}
