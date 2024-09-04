package com.project.payload.response.business;

import com.fasterxml.jackson.annotation.JsonInclude;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdvertResponse {

    private Long id;
    private String title;
    private String description;
    private BigDecimal price;
    private String advertTypeTitle;
    private String countryName;
    private String districtName;
    private String categoryTitle;
    private String userName;
    private String location;
    private String image;
    private boolean isActive;
    private String cityName;


}