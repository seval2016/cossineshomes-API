package com.project.payload.request.business;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.entity.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class AdvertRequest {


    @NotNull(message = " Title must not be empty")
    @Size(min = 5, max = 150)
    private String title;

    @NotNull(message = "Description must not be empty")
    @Size(max = 300)
    private String description;

    @NotNull(message =" Price  must not be empty")
    private BigDecimal price;

    /* title otomatik olarak slug'a dönüştürülebilir kullanıcıdan bir daha slug almaya gerek yok !!!
    @NotNull ( message = " Slug must not be empty")
    @Size(min = 5, max = 200)
    private String slug;*/

    @NotNull(message =" Advert Type must not be empty")
    private Long advertTypeId;

    @NotNull(message =" Country must not be empty")
    private Long countryId;

    @NotNull(message =" City  must not be empty")
    private Long cityId;

    @NotNull(message =" District  must not be empty")
    private Long districtId;

    @NotNull(message =" Category  must not be empty")
    private Long categoryId;

    @NotNull(message =" User must not be empty")
    private Long userId;

    @NotNull(message =" Location  must not be empty")
    private String location;

    @NotNull(message =" Active status must not be empty")
    private boolean isActive;

}