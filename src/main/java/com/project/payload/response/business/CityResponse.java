package com.project.payload.response.business;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class CityResponse {

    private Long id;
    private String name;
}
