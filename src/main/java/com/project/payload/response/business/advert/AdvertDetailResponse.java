package com.project.payload.response.business.advert;

import com.project.entity.concretes.business.Favorite;
import com.project.entity.concretes.business.TourRequest;
import com.project.payload.response.business.ImagesResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class AdvertDetailResponse { //A07 - /adverts/:slug

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

    private List<ImagesResponse> images;

    private ImagesResponse featuredImage;

    private List<Favorite> favoritesList;

    private List<TourRequest> tourRequestList;
}

