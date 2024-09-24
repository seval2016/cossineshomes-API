package com.project.payload.response.business.tourRequest;

import com.project.payload.response.UserResponse;
import com.project.payload.response.business.advert.AdvertResponse;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class TourRequestResponse {
    private Long id; // Tur talebi ID'si
    private LocalDate tourDate; // Tur tarihi
    private LocalTime tourTime; // Tur saati
    private String status; // Talep durumu
    private AdvertResponse advert; // İlgili ilan bilgileri
    private UserResponse ownerUser; // Sahip kullanıcı bilgileri
    private UserResponse guestUser; // Misafir kullanıcı bilgileri

}
