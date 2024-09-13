package com.project.service.business;

import com.project.entity.concretes.business.Country;
import com.project.exception.ResourceNotFoundException;
import com.project.payload.messages.ErrorMessages;
import com.project.payload.response.business.ResponseMessage;

import com.project.repository.business.CountryRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class CountryService {

    private final CountryRepository countryRepository;

    // Ülkeleri sıfırlamak için (deleteAll)
    public void resetCountryTables() {
        countryRepository.deleteAll();
    }

    // ID'ye göre ülke getirme
    public Country getCountryById(Long id) {
        return countryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.COUNTRY_NOT_FOUND));
    }

    // Toplam ülke sayısını döndürme
    public int countAllCountries() {
        return countryRepository.countAllCountries();  // Repository'de `countAllCountries` metodu olduğundan emin olun
    }

    // Türkiye'nin built-in olarak işaretlenmesi
    public void setBuiltInForCountry() {
        // Türkiye'nin ID'si 1 olduğundan emin olun
        Long countryId = 1L;

        Country country = countryRepository.findById(countryId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.COUNTRY_NOT_FOUND));
        country.setBuiltIn(true);  // builtIn adında bir boolean alan eklemelisiniz
        countryRepository.save(country);
    }

    // Tüm ülkeleri getirme
    public ResponseMessage<List<Country>> getAllCountries() {
      List<Country> countryList= countryRepository.findAll();
      return ResponseMessage.<List<Country>>builder()
              .httpStatus(HttpStatus.OK)
              .object(countryList)
              .message("Countries were brought successfully.")
              .build();
    }
}
