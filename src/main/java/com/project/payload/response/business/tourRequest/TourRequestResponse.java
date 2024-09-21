package com.project.payload.response.business.tourRequest;

import com.project.entity.concretes.business.*;
import com.project.entity.concretes.user.User;
import com.project.entity.enums.StatusType;
import com.project.payload.response.business.image.ImageResponse;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private User ownerUserId;
    private User guestUserId;
    private Advert advertId;
    private String advertTitle;
    private ImageResponse featuredImage;
    private District advertDistrict;
    private City advertCity;
    private Country advertCountry;

}
