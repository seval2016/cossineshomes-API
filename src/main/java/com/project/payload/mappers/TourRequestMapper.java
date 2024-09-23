package com.project.payload.mappers;

import com.project.entity.concretes.business.Advert;
import com.project.entity.concretes.business.TourRequest;
import com.project.entity.concretes.user.User;
import com.project.payload.request.business.TourRequestRequest;
import com.project.payload.response.business.tourRequest.TourRequestResponse;
import com.project.service.helper.ImageHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TourRequestMapper {

    private final ImageHelper imageHelper;
    private final AdvertMapper advertMapper;
    private final UserMapper userMapper;



    //POJO -> DTO
    public TourRequestResponse mapTourRequestToTourRequestResponse(TourRequest tourRequest) {
        if (tourRequest == null) {
            return null;
        }

        return TourRequestResponse.builder()
                .id(tourRequest.getId())
                .tourDate(tourRequest.getTourDate())
                .tourTime(tourRequest.getTourTime())
                .status(tourRequest.getStatus().name()) // Enum değerini string olarak döndür
                .advert(advertMapper.mapAdvertToAdvertResponse(tourRequest.getAdvert())) // Advert için bir mapper kullanılıyor
                .ownerUser(userMapper.mapUserToUserResponse(tourRequest.getOwnerUser())) // Owner kullanıcı bilgileri
                .guestUser(userMapper.mapUserToUserResponse(tourRequest.getGuestUser())) // Misafir kullanıcı bilgileri
                .build();
    }

    public TourRequest mapTourRequestRequestToTourRequest(TourRequestRequest request, Advert advert, User ownerUser, User guestUser){
        return TourRequest.builder()
                .tourDate(request.getTourDate())
                .tourTime(request.getTourTime())
                .advert(advert)
                .ownerUser(ownerUser) // Owner kullanıcı bilgileri
                .guestUser(guestUser) // Misafir kullanıcı bilgileri
                .build();
    }

}
