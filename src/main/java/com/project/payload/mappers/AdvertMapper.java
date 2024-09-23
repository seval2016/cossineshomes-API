package com.project.payload.mappers;


import com.project.entity.concretes.business.*;
import com.project.entity.concretes.business.Image;
import com.project.entity.concretes.user.User;
import com.project.entity.enums.AdvertStatus;
import com.project.payload.request.business.AdvertRequest;

import com.project.payload.response.business.advert.AdvertDetailsForSlugResponse;
import com.project.payload.response.business.advert.AdvertListResponse;
import com.project.payload.response.business.advert.AdvertResponse;
import com.project.payload.response.business.advert.AdvertResponseForUser;
import com.project.payload.response.business.category.CategoryAdvertResponse;
import com.project.payload.response.business.category.CategoryPropertyValueResponse;
import com.project.payload.response.business.image.ImageResponse;
import com.project.payload.response.business.tourRequest.TourRequestResponseForSlug;
import com.project.service.helper.AdvertHelper;
import com.project.service.helper.MethodHelper;
import lombok.*;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@Component
public class AdvertMapper {

    private final TourRequestMapper tourRequestMapper;
    private final MethodHelper methodHelper;
    private final ImageMapper imageMapper;
    private final CategoryPropertyValueMapper categoryPropertyValueMapper;
    private final AdvertHelper advertHelper;

    public Advert mapAdvertRequestToAdvert(AdvertRequest advertRequest, Category category, City city, User user, Country country, AdvertType advertType, District district) {
        return Advert.builder()
                .title(advertRequest.getTitle())
                .description(advertRequest.getDescription())
                .price(advertRequest.getPrice())
                .location(advertRequest.getLocation())
                .viewCount(0)
                .status(AdvertStatus.PENDING.getValue())
                .category(category)
                .country(country)
                .city(city)
                .user(user)
                .advertType(advertType)
                .district(district)
                .build();
    }

    private String generateSlug(String title) {
        return title.toLowerCase().replaceAll(" ", "-");
    }


    public AdvertDetailsForSlugResponse mapAdvertToAdvertResponseForSlug(Advert advert) {
        return AdvertDetailsForSlugResponse.builder()
                .id(advert.getId())
                .title(advert.getTitle())
                .properties(advert.getCategoryPropertyValuesList().stream()
                        .map(propertyValue -> new CategoryPropertyValueResponse(
                                propertyValue.getCategoryPropertyKey().getId(),
                                propertyValue.getValue()
                        ))
                        .collect(Collectors.toList()))
                .images(advert.getImages().stream()
                        .map(image -> new ImageResponse(
                                image.getId(),
                                image.getName(),
                                image.getType(),
                                image.getFeatured()
                        ))
                        .collect(Collectors.toList()))
                .tourRequests(advert.getTourRequestList().stream()
                        .map(tourRequestForSlug -> new TourRequestResponseForSlug(
                                tourRequestForSlug.getId(),
                                tourRequestForSlug.getTourDate(),
                                tourRequestForSlug.getTourTime()
                        ))
                        .collect(Collectors.toList())
                )
                .build();
    }

    public AdvertResponse mapAdvertToAdvertResponseForAll(Advert advert) {
        return AdvertResponse.builder()
                .id(advert.getId())
                .userId(advert.getUser().getId())
                .price(advert.getPrice())
                .slug(advert.getSlug())
                .builtIn(advert.getIsActive())
                .description(advert.getDescription())
                .title(advert.getTitle())
                .status(advertHelper.updateAdvertStatus(advert.getStatus(), advert))
                .countryId(advert.getCountry().getId())
                .cityId(advert.getCity().getId())
                .districtId(advert.getDistrict().getId())
                .createAt(advert.getCreateAt())
                .advertTypeId(advert.getAdvertType().getId())
                .categoryId(advert.getCategory().getId())
                .categoryPropertyKeys(advert.getCategory().getCategoryPropertyKeys())
                .featuredImage(advertHelper.getFeaturedImage(advert.getImages()))
                .images(advert.getImages().stream()
                        .map(imageMapper::mapToImageResponse)
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
                .slug(generateSlug(advertRequest.getTitle()))  // Slug alanı eklenmeli
                .build();
    }

    //For Category POJO==>DTO
    public CategoryAdvertResponse mapCategoryToCategoryForAdvertResponse(Category category) {
        return CategoryAdvertResponse.builder()
                .category(category.getTitle())
                .amount(category.getAdverts().size())
                .build();
    }


    //****************************

    public AdvertListResponse toAdvertListResponse(Advert advert) {
        return AdvertListResponse.builder()
                .id(advert.getId())
                .title(advert.getTitle())
                //.featuredImage(getFeaturedImage(advert))
                .build();
    }

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
                .categoryPropertyKeys(advert.getCategory().getCategoryPropertyKeys())
                .featuredImage(advertHelper.getFeaturedImage(advert.getImages()))
                .images(advert.getImages().stream()
                        .map(imageMapper::mapToImageResponse)
                        .collect(Collectors.toList()))
                .favoritesCount(advert.getFavoritesList().size())
                .tourRequestCount(advert.getTourRequestList().size())
                .isActive(advert.getIsActive())
                .build();
    }
    public AdvertResponseForUser mapAdvertToAdvertResponseForUser(Advert advert){ //A08

        // Dinamik özellikleri saklamak için bir harita oluştur
        Map<String, Object> properties = new HashMap<>();

        // Advert'ın özelliklerini dinamik olarak haritaya ekle
        for (CategoryPropertyValue cpv : advert.getCategoryPropertyValuesList()) {
            properties.put(cpv.getCategoryPropertyKey().getName(), cpv.getValue());
        }

        // Resim ve tur taleplerini hazırlama
        List<String> imageUrls = advert.getImages().stream()
                .map(Image::getUrl) // Resim URL'lerini al
                .collect(Collectors.toList());

        List<String> tourRequests = advert.getTourRequestList().stream()
                .map(tourRequest -> String.format("Date: %s, Time: %s, Status: %s",
                        tourRequest.getTourDate(), tourRequest.getTourTime(), tourRequest.getStatus()))
                .collect(Collectors.toList());

        return AdvertResponseForUser.builder()
                .id(advert.getId())
                .userId(advert.getUser().getId())
                .title(advert.getTitle())
                .description(advert.getDescription())
                .slug(advert.getSlug())
                .price(advert.getPrice())
                .status(advert.getStatus())
                .builtIn(advert.getBuiltIn())
                .isActive(advert.getIsActive())
                .viewCount(advert.getViewCount())
                .location(advert.getLocation())
                .createAt(advert.getCreateAt())
                .updateAt(advert.getUpdateAt())
                .properties(properties) // Dinamik özellikleri ekle
                .images(imageUrls) // Resim URL'lerini ekle
                .tourRequests(tourRequests) // Tur taleplerini ekle
                .build();
    }

}

