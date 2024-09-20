package com.project.payload.response.business.advert;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.project.payload.response.business.ImagesResponse;
import com.project.payload.response.business.PropertyResponse;
import com.project.payload.response.business.TourRequestResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdvertDetailsForSlugResonse {
    private Long id;
    private String title;                      // Başlık (title)
    private List<PropertyResponse> properties; // İlanın property bilgileri
    private List<ImagesResponse> images;       // Resim bilgileri
    private List<TourRequestResponse> tourRequests; // Tur talepleri
}
