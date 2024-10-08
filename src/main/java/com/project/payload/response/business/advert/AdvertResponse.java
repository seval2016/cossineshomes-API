package com.project.payload.response.business.advert;

import com.fasterxml.jackson.annotation.JsonInclude;

import com.project.entity.concretes.business.CategoryPropertyKey;
import com.project.entity.concretes.business.Favorite;
import com.project.entity.concretes.business.TourRequest;
import com.project.payload.response.business.category.CategoryPropertyValueResponse;
import com.project.payload.response.business.image.ImageResponse;
import lombok.*;

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

    private BigDecimal price;

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


    public AdvertResponse(Long id, String title, String description, String image, BigDecimal price,int status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.status=status;
    }

}