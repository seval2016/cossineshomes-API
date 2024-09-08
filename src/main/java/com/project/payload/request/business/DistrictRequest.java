package com.project.payload.request.business;

import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class DistrictRequest {

    @NotNull(message = "District name can not be null")
    @Size(min = 2, max = 30, message = "District name should have min 2 chars and max 30 chars")
    @Pattern(regexp = "\\A(?!\\s*\\Z).+", message = "District name must consist of the characters .")
    private String name;

    @NotNull(message = "City id can not be null")
    private int district_id;
}

