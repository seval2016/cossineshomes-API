package com.project.payload.mappers;

import com.project.entity.concretes.business.Advert;
import com.project.entity.concretes.business.Images;
import com.project.entity.concretes.business.TourRequest;
import com.project.entity.concretes.user.User;
import com.project.entity.enums.TourRequestStatus;
import com.project.payload.request.business.TourRequestRequest;
import com.project.payload.response.business.TourRequestResponse;
import com.project.service.helper.MethodHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TourRequestMapper {

    private final ImagesMapper imagesMapper;

    private Images getFeaturedImage(List<Images> images) {
        return images.stream()
                .filter(Images::isFeatured)
                .findFirst()
                .orElse(images.get(0));
    }

    //POJO -> DTO
    public TourRequestResponse mapTourRequestToTourRequestResponse(TourRequest tourRequest) {
        Advert advert = tourRequest.getAdvert();

        // İlanın resim listesinden öne çıkan resmi alıyoruz, null kontrolü eklenmiştir
        Images featuredImage = advert != null && advert.getImagesList() != null
                ? getFeaturedImage(advert.getImagesList())
                : null;

        return TourRequestResponse.builder()
                .advertId(tourRequest.getAdvert())
                .advertTitle(tourRequest.getAdvert().getTitle())
                .advertCountry(tourRequest.getAdvert().getCountry())
                .advertCity(tourRequest.getAdvert().getCity())
                .advertDistrict(tourRequest.getAdvert().getDistrict())
                .featuredImage(imagesMapper.mapToImageResponse(getFeaturedImage(tourRequest.getAdvert().getImagesList())))
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

    public TourRequest mapTourRequestRequestToTourRequest(TourRequestRequest request, Advert advert, User ownerUser, User guestUser){
        return TourRequest.builder()
                .tourDate(request.getTourDate())
                .tourTime(request.getTourTime())
                .advert(advert)
                .ownerUser(ownerUser)
                .guestUser(guestUser)
                .status(TourRequestStatus.PENDING)
                .createAt(LocalDate.now())
                .build();
    }
}
