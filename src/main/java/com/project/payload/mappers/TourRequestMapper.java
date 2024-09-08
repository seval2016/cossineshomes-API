package com.project.payload.mappers;

import com.project.entity.concretes.business.Advert;
import com.project.entity.concretes.business.TourRequest;
import com.project.entity.concretes.user.User;
import com.project.entity.enums.TourRequestStatus;
import com.project.payload.request.business.TourRequestRequest;
import com.project.payload.response.UserResponse;
import com.project.payload.response.business.AdvertResponse;
import com.project.payload.response.business.AdvertResponseForTourRequest;
import com.project.payload.response.business.TourRequestResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TourRequestMapper {

    //POJO -> DTO
    public TourRequestResponse mapTourRequestToTourRequestResponse(TourRequest tourRequest) {
        if (tourRequest == null) {
            return null;
        }

        Advert advert = tourRequest.getAdvert();

        return TourRequestResponse.builder()
                .id(tourRequest.getId())
                .tourDate(tourRequest.getTourDate())
                .tourTime(tourRequest.getTourTime())
                .status(tourRequest.getStatus())
                .createAt(tourRequest.getCreateAt().atStartOfDay())
                .updateAt(advert.getUpdateAt())
                .ownerUserId(tourRequest.getOwnerUser())
                .guestUserId(tourRequest.getGuestUser())
                .advertId(advert)
                .advertTitle(advert.getTitle())
                .featuredImage(advert.getFeaturedImage())
                .advertDistrict(advert.getDistrict())
                .advertCity(advert.getCity())
                .advertCountry(advert.getCountry())
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
