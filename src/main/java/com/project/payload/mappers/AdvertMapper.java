package com.project.payload.mappers;

import com.project.repository.business.entity.concretes.business.*;
import com.project.repository.business.entity.concretes.user.User;
import com.project.repository.business.entity.enums.Status;
import com.project.payload.request.business.AdvertRequest;
import com.project.payload.response.business.AdvertResponse;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Data
@Component
public class AdvertMapper {

    // Yeni bir Advert nesnesi oluşturur (DTO -> POJO)
    public Advert mapAdvertRequestToAdvert(AdvertRequest advertRequest, AdvertType advertType, Country country, City city, District district, Category category, User user){
        return Advert.builder()
                .title(advertRequest.getTitle())
                .description(advertRequest.getDescription())
                .price(advertRequest.getPrice())
                .advertType(advertType)
                .country(country)
                .city(city)
                .district(district)
                .category(category)
                .user(user)
                .location(advertRequest.getLocation())
                .isActive(advertRequest.isActive())
                .builtIn(false)
                .createAt(LocalDateTime.now())
                .status(Status.PENDING)
                .viewCount(0)
                .slug(generateSlug(advertRequest.getTitle()))
                .build();

    }

    private String generateSlug(String title) {
        return title.toLowerCase().replaceAll(" ", "-");
    }


    // Advert nesnesini AdvertResponse'a dönüştürür (POJO -> DTO)
    public AdvertResponse mapAdvertToAdvertResponse(Advert advert){

        return AdvertResponse.builder()
                .id(advert.getId())
                .title(advert.getTitle())
                .description(advert.getDescription())
                .price(advert.getPrice())
                .advertTypeTitle(advert.getAdvertType().getTitle())
                .countryName(advert.getCountry().getName())
                .cityName(advert.getCity().getName())
                .districtName(advert.getDistrict().getName())
                .categoryTitle(advert.getCategory().getTitle())
                .userName(advert.getUser().getUsername())
                .location(advert.getLocation())
                .isActive(advert.isActive())
                .build();
    }

}