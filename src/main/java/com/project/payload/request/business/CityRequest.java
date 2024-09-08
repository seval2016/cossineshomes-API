package com.project.payload.request.business;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class CityRequest {


    @NotNull(message = "City name can not be empty")
    @Size(max = 30, message = "City name should be at most 30 chars")
    private String name;


    @NotNull(message = "Country id can not be empty")
    private int country_id;



}