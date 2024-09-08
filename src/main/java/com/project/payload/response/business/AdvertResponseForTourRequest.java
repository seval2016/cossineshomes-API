package com.project.payload.response.business;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class AdvertResponseForTourRequest {
    private Long id;
    private String title;
}
