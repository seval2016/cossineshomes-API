package com.project.payload.mappers;

import com.project.entity.concretes.business.District;
import com.project.payload.response.business.DistrictResponse;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class DistrictMapper {

    public DistrictResponse mapDistrictToDistrictResponse (District district){
        return DistrictResponse.builder()
                .id(district.getId())
                .name(district.getName())
                .build();
    }
}
