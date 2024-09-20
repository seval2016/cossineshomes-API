package com.project.payload.response.business.advert;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.project.payload.response.business.ImagesResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PopularAdvertResponse { //A04 - /adverts/popular/:amount

    private Long id;

    private String title;

    private String slug;

    private BigDecimal price;

    private Integer viewCount;

    private int tourRequestCount;

    private int popularityPoint;

    private ImagesResponse featuredImage;
}
