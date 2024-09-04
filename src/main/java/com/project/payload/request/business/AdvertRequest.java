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

    @NotNull
    @Size(max = 300)
    private String description;

    @NotNull
    private BigDecimal price;

    /* title otomatik olarak slug'a dönüştürülebilir kullanıcıdan bir daha slug almaya gerek yok !!!
    @NotNull ( message = " Slug must not be empty")
    @Size(min = 5, max = 200)
    private String slug;*/

    @NotNull
    private Long advertTypeId;

    @NotNull
    private Integer countryId;

    @NotNull
    private Long cityId;

    @NotNull
    private Long districtId;

    @NotNull
    private Long categoryId;

    @NotNull
    private Long userId;

    @NotNull
    private String location;

    @NotNull
    private List<PropertyRequest> properties;

    @NotNull
    private boolean isActive;

}