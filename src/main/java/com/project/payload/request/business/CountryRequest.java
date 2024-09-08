package com.project.payload.request.business;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class CountryRequest {

    @NotNull(message = "Country name must not be empty")
    @Size(max = 30, message = "Country name should be at most 30 chars")
    @Pattern(regexp = "\\A(?!\\s*\\Z).+", message = "Title must consist of the characters .")
    private String name;


}