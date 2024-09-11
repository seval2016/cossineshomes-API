package com.project.payload.mappers;

import com.project.entity.concretes.business.*;
import com.project.entity.concretes.user.User;
import com.project.entity.enums.AdvertStatus;
import com.project.payload.request.business.AdvertRequest;
import com.project.payload.response.business.AdvertResponse;
import com.project.service.helper.MethodHelper;
import lombok.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Component
public class AdvertMapper {

    private final TourRequestMapper tourRequestMapper;
    private final MethodHelper methodHelper;
    private final ImagesMapper imagesMapper;

    // Yeni bir Advert nesnesi oluşturur (DTO -> POJO)
    public Advert mapAdvertRequestToAdvert(AdvertRequest advertRequest, AdvertType advertType, Country country, City city, District district, Category category, User user, List<Images> images) {
         return Advert.builder()
                .title(advertRequest.getTitle())
                .description(advertRequest.getDescription())
                .location(advertRequest.getLocation())
                .price(advertRequest.getPrice())
                .viewCount(0)
                .district(district)
                .category(category)
                .status(AdvertStatus.PENDING.getValue())
                .city(city)
                .user(user)
                .country(country)
                .advertType(advertType)
                 .imagesList(images)
                 .build();

    }

    private String generateSlug(String title) {
        return title.toLowerCase().replaceAll(" ", "-");
    }

    // Advert nesnesini AdvertResponse'a dönüştürür (POJO -> DTO)
    public AdvertResponse mapAdvertToAdvertResponse(Advert advert) {

        return AdvertResponse.builder()
                .id(advert.getId())
                .title(advert.getTitle())
                .userId(advert.getUser().getId())
                .description(advert.getDescription())
                .price(advert.getPrice())
                .advertTypeId(advert.getAdvertType().getId())
                .countryId(advert.getCountry().getId())
                .cityId(advert.getCity().getId())
                .districtId(advert.getDistrict().getId())
                .categoryId(advert.getCategory().getId())
                .location(advert.getLocation())
                .categoryId(advert.getCategory().getId())
                .categoryPropertyKeys(advert.getCategory().getCategoryPropertyKeys())
                .featuredImage(imagesMapper.mapToImageResponse(getFeaturedImage(advert.getImagesList())))
                .images(advert.getImagesList().stream()
                        .map(imagesMapper::mapToImageResponse)
                        .collect(Collectors.toList()))
                .favoritesCount(advert.getFavoritesList().size())
                .tourRequestCount(advert.getTourRequestList().size())
                .isActive(advert.isActive())
                .build();
    }

    private Images getFeaturedImage(List<Images> images) {
        return images.stream()
                .filter(Images::isFeatured)
                .findFirst()
                .orElse(images.get(0));
    }

    public AdvertResponse mapAdvertToAdvertResponseForAll(Advert advert) {
        return AdvertResponse.builder()
                .id(advert.getId())
                .userId(advert.getUser().getId())
                .price(advert.getPrice())
                .slug(advert.getSlug())
                .builtIn(advert.isBuiltIn())
                .description(advert.getDescription())
                .title(advert.getTitle())
                .status(methodHelper.updateAdvertStatus(advert.getStatus(), advert))
                .countryId(advert.getCountry().getId())
                .cityId(advert.getCity().getId())
                .districtId(advert.getDistrict().getId())
                .createAt(advert.getCreateAt())
                .advertTypeId(advert.getAdvertType().getId())
                .categoryId(advert.getCategory().getId())
                .categoryPropertyKeys(advert.getCategory().getCategoryPropertyKeys())
                .featuredImage(imagesMapper.mapToImageResponse(getFeaturedImage(advert.getImagesList())))
                .images(advert.getImagesList().stream()
                        .map(imagesMapper::mapToImageResponse)
                        .collect(Collectors.toList()))
                .favoritesCount(advert.getFavoritesList().size())
                .tourRequestCount(advert.getTourRequestList().size())
                .build();

    }

    public Advert mapAdvertRequestToUpdateAdvert(Long id, AdvertRequest advertRequest, Category category, City city, Country country, AdvertType advertType, District district, User user) {
        return Advert.builder()
                .id(id)
                .user(user)
                .country(country)
                .builtIn(false)
                .viewCount(0)
                .city(city)
                .advertType(advertType)
                .price(advertRequest.getPrice())
                .title(advertRequest.getTitle())
                .description(advertRequest.getDescription())
                .district(district)
                .status(AdvertStatus.PENDING.getValue())
                .category(category)
                .location(advertRequest.getLocation())
                .build();
    }

}