package com.project.payload.response.business.advert;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class CityAdvertResponse {  //A02 - /adverts/cities

    private String city;
    private long advertCount;
}
