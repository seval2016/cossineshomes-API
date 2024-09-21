package com.project.payload.response.business.advert;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.project.entity.concretes.business.Favorite;
import com.project.entity.concretes.business.TourRequest;
import com.project.payload.response.business.image.ImageResponse;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthAdvertUpdateResponse { // A11 - /adverts/auth/:id

    private Long id;

    private String title;

    private String slug;

    private BigDecimal price;

    private int status;

    private Boolean isActive;

    private LocalDateTime updateAt;

    private List<ImageResponse> images;

    private List<Favorite> favoritesList;

    private List<TourRequest> tourRequestList;
}