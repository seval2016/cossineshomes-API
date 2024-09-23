package com.project.payload.response.authentication;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.project.payload.response.business.advert.AdvertResponse;
import com.project.payload.response.business.tourRequest.TourRequestResponse;
import lombok.*;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponse {

    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String phone;

    private Set<String> userRole;

    private String token;

    private Boolean builtIn = false;
}
