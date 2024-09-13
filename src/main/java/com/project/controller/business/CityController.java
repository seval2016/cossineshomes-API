package com.project.controller.business;

import com.project.entity.concretes.business.City;
import com.project.payload.response.ResponseMessage;

import com.project.service.business.CityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/cities")
@RequiredArgsConstructor
public class CityController {


    private final CityService cityService;


    @GetMapping("/getAll") //http://localhost:8080/city/getAll
    public ResponseMessage<List<City>> getAllCity(){
        List<City> cityList =cityService.getAllCity();
        return ResponseMessage.<List<City>>builder()
                .status(HttpStatus.OK)
                .object(cityList)
                .build();
    }
    @GetMapping("/getByCity/{countryId}") //http://localhost:8080/city/getByCity/1
    public ResponseEntity<List<City>> getCityById(@PathVariable Long countryId){
        return cityService.getCityById(countryId);
    }
}
