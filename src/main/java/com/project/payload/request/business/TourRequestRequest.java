package com.project.payload.request.business;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class TourRequestRequest {

    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern ="dd-MM-yyyy" )
    @NotNull(message = "Tour date can not be empty")
    private LocalDate tourDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    @NotNull(message = "Tour date can not be empty")
    private LocalTime tourTime;

    @NotNull(message = "Advert can not be empty")
    private Long advertId;
}
