package com.project.payload.response.business.advert;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.project.payload.response.business.ImagesResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdvertListResponse { // A01 - /Adverts
    private Long id;
    private String title;
    private ImagesResponse featuredImage;
}
