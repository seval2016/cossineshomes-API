package com.project.payload.request.business;

import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateAdvertRequest {

    @NotNull
    @Size(max = 150)
    private String title;

    private String description;

    private Long cityId;

    private Long countryId;

    private Long categoryId;

    private Long districtId;

    private Long advertTypeId;

    private Double price;

    private String location;
}
