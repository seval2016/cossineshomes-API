package com.project.payload.mappers;

import com.project.entity.concretes.business.Advert;
import com.project.payload.request.business.AdvertRequest;
import com.project.payload.response.business.AdvertResponse;
import com.project.repository.business.AdvertRepository;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class AdvertMapper {

    // DTO -> POJO
    public Advert mapAdvertRequestToAdvert(AdvertRequest advertRequest){
        return Advert.builder()
                .title(advertRequest.getTitle())
                .description(advertRequest.getDescription())
                .price(advertRequest.getPrice())
                .status(advertRequest.getStatus())
                .viewCount(advertRequest.getViewCount())
                .location(advertRequest.getLocation())
                .build();

    }

    // POJO -> DTO

    public AdvertResponse mapAdvertToAdvertResponse(Advert advert){

        return AdvertResponse.builder()
                .advertId(advert.getId())
                .title(advert.getTitle())
                .description(advert.getDescription())
                .slug(advert.getSlug())
                .price(advert.getPrice())
                .status(advert.getStatus())
                .isActive(advert.isActive())
                .location(advert.getLocation())
                .viewCount(advert.getViewCount())
                .build();

    }

    // Update icin kullaniyoruz
    public Advert mapAdvertRequestToUpdatedAdvert(Long id, AdvertRequest advertRequest){
        return mapAdvertRequestToAdvert(advertRequest)
                .toBuilder()
                .id(id)
                .build();

    }




}