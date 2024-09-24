package com.project.payload.response.business;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class CityResponse {

    private Long id;
    private String name;
    private Long CountryId;
    private long amount; // Bu alan sadece DTO'da yer alacak ve her şehir için reklam sayısını döndürecektir.
}
