package com.project.payload.request.business;

import lombok.*;
import javax.validation.constraints.*;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class AdvertRequest {


    @NotNull(message = "Title must not be empty")
    @Size(min = 5, max = 150, message = "Title should be at least 5 chars")
    @Pattern(regexp = "\\A(?!\\s*\\Z).+", message = "Title must consist of the characters .")
    private String title;

    @NotNull(message = "Description must not be empty")
    @Size(max = 300, message = "Description should be at max 300 chars")
    @Pattern(regexp = "\\A(?!\\s*\\Z).+", message = "Description must consist of the characters .")
    private String description;

    @NotNull(message = "Price must not be empty")
    @Min(value = 1)
    private Double price;

    @NotNull(message = "Please enter location")
    private String location;

    @NotNull(message = "Advert type ID is required")
    @Min(value = 1, message = "Advert type ID must be greater than or equal to 1")
    private Long advertTypeId;

    @NotNull(message = "Country ID is required")
    @Min(value = 1, message = "Country ID must be greater than or equal to 1")
    private Long countryId;

    @NotNull(message = "City ID is required")
    @Min(value = 1, message = "City ID must be greater than or equal to 1")
    private Long cityId;

    @NotNull(message = "District ID is required")
    @Min(value = 1, message = "District ID must be greater than or equal to 1")
    private Long districtId;

    @NotNull(message = "Category ID is required")
    @Min(value = 1, message = "Category ID must be greater than or equal to 1")
    private Long categoryId;

    @NotNull(message = "User ID is required")
    @Min(value = 1, message = "User ID must be greater than or equal to 1")
    private Long userId;

    @NotNull
    private List<PropertyRequest> properties;

}