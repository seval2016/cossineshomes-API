package com.project.payload.response.business.advert;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.project.payload.response.business.category.PropertyValueResponse;
import com.project.payload.response.business.image.ImageResponse;
import com.project.payload.response.business.tourRequest.TourRequestResponse;
import com.project.payload.response.business.tourRequest.TourRequestResponseForSlug;
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
public class AdvertDetailsForSlugResponse {//A07
    private Long id;
    private String title;
    private List<PropertyValueResponse> properties; // property listesi için DTO kullanıyoruz
    private List<ImageResponse> images;        // image'lar için ayrı bir DTO
    private List<TourRequestResponseForSlug> tourRequests;
}
