package com.project.payload.request.business;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class AdvertRequest {


    @NotBlank
    @Size(min = 5, max = 150)
    private String title;

    @NotBlank
    @Size(max = 300)
    private String description;

    @NotNull
    private Double price;

    @NotNull
    private Long advertTypeId;

    @NotNull
    private Long countryId;

    @NotNull
    private Long cityId;

    @NotNull
    private Long districtId;

    @NotNull
    private Long categoryId;

    @NotNull
    private Long userId;

    @NotBlank
    private String location;

    @NotNull
    private List<PropertyRequest> properties;

    @NotNull
    private boolean isActive;

    @NotNull
    private List<Long> imageIds;

}