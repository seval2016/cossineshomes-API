package com.project.payload.response.business.tourRequest;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class TourRequestResponseForSlug {
    private Long id;
    private LocalDate tourDate;
    private LocalTime tourTime;
}
