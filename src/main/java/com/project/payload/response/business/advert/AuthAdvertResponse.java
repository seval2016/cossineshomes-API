package com.project.payload.response.business.advert;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.project.entity.concretes.business.Favorite;
import com.project.entity.concretes.business.TourRequest;
import com.project.payload.response.business.ImagesResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthAdvertResponse { //A05 - /adverts/auth

    private Long id;

    private String title;

    private String slug;

    private BigDecimal price;

    private int status;

    private Boolean isActive;

    private LocalDateTime createAt;

    private LocalDateTime updateAt;

    private List<ImagesResponse> images;

    private List<Favorite> favoritesList;

    private List<TourRequest> tourRequestList;
}
