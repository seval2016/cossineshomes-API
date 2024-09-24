package com.project.service.business;


import com.project.entity.concretes.business.City;
import com.project.entity.concretes.business.District;
import com.project.exception.ResourceNotFoundException;
import com.project.payload.mappers.DistrictMapper;

import com.project.payload.messages.ErrorMessages;
import com.project.payload.request.business.CityRequest;
import com.project.payload.request.business.DistrictRequest;
import com.project.payload.response.business.DistrictResponse;
import com.project.payload.response.business.ResponseMessage;
import com.project.repository.business.CityRepository;
import com.project.repository.business.DistrictRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DistrictService {

    private final DistrictRepository districtRepository;
    private final DistrictMapper districtMapper;
    private final CityService cityService;
    private final CityRepository cityRepository;

    public List<DistrictResponse> getAllDistrict() {
        return districtRepository.findAll()
                .stream()
                .map(districtMapper::mapDistrictToDistrictResponse)
                .collect(Collectors.toList());
    }

    public ResponseMessage<List<District>> getByDistrict(Long cityId) {
        // Belirtilen cityId'ye ait ilçeleri getir
        List<District> districtList = districtRepository.getByDistrict(cityId);

        return ResponseMessage.<List<District>>builder()
                .httpStatus(HttpStatus.OK)
                .object(districtList)
                .message("Districts were brought successfully.")
                .build();
    }

    public District getDistrictByIdForAdvert(Long districtId) {
        return districtRepository.findById(districtId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.DISTRICT_NOT_FOUND + ": " + districtId));
    }

    public void resetDistrictTables() {
        districtRepository.deleteAll();
    }

    public int countAllDistricts() {
        return districtRepository.countAllDistricts();
    }

    public void setBuiltInForDistrict() {
        DistrictRequest defaultDistrict = new DistrictRequest();
        defaultDistrict.setName("Üsküdar");
        defaultDistrict.setCityId(1L);
        saveDistrict(defaultDistrict);
    }

    public District saveDistrict(DistrictRequest districtRequest) {
        // City exists check
        City city = cityRepository.findById((long) districtRequest.getCityId())
                .orElseThrow(() -> new RuntimeException("City with ID " + districtRequest.getCityId() + " does not exist."));

        // Build the District entity from the request
        District district = District.builder()
                .name(districtRequest.getName())
                .city(city)
                .builtIn(false)  // Default value, adjust as necessary
                .build();

        // Save the district entity to the database
        return districtRepository.save(district);
    }
}
