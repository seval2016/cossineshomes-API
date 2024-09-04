package com.project.service.business;

import com.project.repository.business.entity.concretes.business.District;
import com.project.payload.response.business.DistrictResponse;
import com.project.repository.business.DistrictRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DistrictService {

    private DistrictRepository districtRepository;

    public List<DistrictResponse> getAllDistricts() {
        List<District> districts = districtRepository.findAll();
        return districts.stream().map(district -> {
            DistrictResponse dto = new DistrictResponse();
            dto.setId(district.getId());
            dto.setName(district.getName());
            dto.setCityId(district.getCity().getId());
            return dto;
        }).collect(Collectors.toList());
    }
}
