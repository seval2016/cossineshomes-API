package com.project.payload.response.business;

import com.fasterxml.jackson.annotation.JsonInclude;


import com.project.entity.concretes.business.CategoryPropertyKey;
import com.project.entity.concretes.business.Favorite;
import com.project.entity.concretes.business.TourRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdvertResponse {


    private Long id;

    private String title;

    private String description;

    private String slug;

    private Double price;

    private int status;

    private Boolean builtIn;

    private Boolean isActive;

    private Integer viewCount;

    private String location;

    private Long advertTypeId;

    private Long countryId;

    private Long cityId;

    private Long districtId;

    private Long userId;

    private Long categoryId;

    private LocalDateTime createAt;

    private LocalDateTime updateAt;

    private Map<String,String > properties;

    private List<ImageResponse> images;

    private ImageResponse featuredImage;

    private List<Favorite> favoritesList;

    private List<TourRequest> tourRequestList;

    private Set<CategoryPropertyKey> categoryPropertyKeys;

    private int tourRequestCount;
    private int favoritesCount;


    // Ek Constructor (Sadece id ve title için)
    public AdvertResponse(Long id, String title) {
        this.id = id;
        this.title = title;
    }

}