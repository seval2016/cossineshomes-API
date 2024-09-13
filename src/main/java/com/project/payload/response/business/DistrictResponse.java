package com.project.payload.response.business;

import com.project.entity.concretes.business.City;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class DistrictResponse {

    private Long id;
    private String name;
    private City city;
}
