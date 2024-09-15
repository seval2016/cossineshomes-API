package com.project.payload.mappers;

import com.project.entity.concretes.business.Advert;
import com.project.entity.concretes.business.Images;
import com.project.entity.concretes.business.TourRequest;
import com.project.payload.request.business.TourRequestRequest;
import com.project.payload.response.business.TourRequestResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TourRequestMapper {

    private final ImageMapper imageMapper;

    private Images getFeaturedImage(List<Images> images) {
        return images.stream()
                .filter(Images::isFeatured)
                .findFirst()
                .orElse(images.get(0));
    }

    //POJO -> DTO
    public TourRequestResponse mapTourRequestToTourRequestResponse(TourRequest tourRequest) {
        return TourRequestResponse.builder()
                .advertId(tourRequest.getAdvert())
                .advertTitle(tourRequest.getAdvert().getTitle())
                .advertCountry(tourRequest.getAdvert().getCountry())
                .advertCity(tourRequest.getAdvert().getCity())
                .advertDistrict(tourRequest.getAdvert().getDistrict())
                .featuredImage(imageMapper.mapToImageResponse(getFeaturedImage(tourRequest.getAdvert().getImagesList())))
                .tourDate(tourRequest.getTourDate())
                .tourTime(tourRequest.getTourTime())
                .guestUserId(tourRequest.getGuestUser())
                .ownerUserId(tourRequest.getOwnerUser())
                .status(tourRequest.getStatus())
                .id(tourRequest.getId())
                .createAt(tourRequest.getCreateAt())
                .updateAt(tourRequest.getUpdateAt())
                .build();
    }

    public TourRequest mapTourRequestRequestToTourRequest(TourRequestRequest request, Advert advert){
        return TourRequest.builder()
                .tourDate(request.getTourDate())
                .tourTime(request.getTourTime())
                .advert(advert)
                .build();
    }

}
