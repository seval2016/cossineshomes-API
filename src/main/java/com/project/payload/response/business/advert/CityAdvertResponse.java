package com.project.payload.response.business.advert;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty("amount")
    private long advertCount; //advertCount alanını JSON yanıtında "amount" olarak serileştirir.
}
