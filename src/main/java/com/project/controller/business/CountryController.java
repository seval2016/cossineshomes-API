package com.project.controller.business;


import com.project.entity.concretes.business.Country;
import com.project.payload.response.business.ResponseMessage;

import com.project.service.business.CountryService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/countries")
@RequiredArgsConstructor
public class CountryController {

    private final CountryService countryService;

    @GetMapping("/countries") // Tüm ülkeleri getirme
    @PreAuthorize("permitAll()")//http://localhost:8080/countries
    public ResponseMessage<List<Country>> getAllCountries() {
        return countryService.getAllCountries();
    }

    // Türkiye'nin built-in olarak işaretlenmesi
    @PostMapping("/builtin")
    public ResponseEntity<Void> setBuiltInForCountry() {
        countryService.setBuiltInForCountry();
        return ResponseEntity.ok().build();
    }
    // Toplam ülke sayısını döndürme
    @GetMapping("/count")
    public ResponseEntity<Integer> countAllCountries() {
        int count = countryService.countAllCountries();
        return ResponseEntity.ok(count);

    }
    // Ülkeleri sıfırlamak için (deleteAll)
    @DeleteMapping("/reset")
    public ResponseEntity<Void> resetCountryTables() {
        countryService.resetCountryTables();
        return ResponseEntity.ok().build();
    }

    // ID'ye göre ülke getirme
    @GetMapping("/{id}")
    public ResponseEntity<Country> getCountryById(@PathVariable Long id) {
        Country country = countryService.getCountryById(id);
        return ResponseEntity.ok(country);
    }
}