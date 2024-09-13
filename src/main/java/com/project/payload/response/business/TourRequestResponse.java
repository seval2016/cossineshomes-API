package com.project.payload.response.business;

import com.project.entity.concretes.business.*;
import com.project.entity.concretes.user.User;
import com.project.entity.enums.StatusType;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class TourRequestResponse {
    private Long id;
    private LocalDate tourDate;
    private LocalTime tourTime;
    private StatusType status;
    private LocalDate createAt;
    private LocalDate updateAt;
    private User ownerUserId;
    private User guestUserId;
    private Advert advertId;
    private String advertTitle;
    private ImagesResponse featuredImage;
    private District advertDistrict;
    private City advertCity;
    private Country advertCountry;

}
