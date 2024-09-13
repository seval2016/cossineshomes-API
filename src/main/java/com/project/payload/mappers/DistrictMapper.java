package com.project.payload.mappers;

public class DistrictMapper {

    public DistrictResponse mapDistrictToDistrictResponse (District district){
        return DistrictResponse.builder()
                .id(district.getId())
                .name(district.getName())
                .city(district.getCity())
                .build();
    }
}
