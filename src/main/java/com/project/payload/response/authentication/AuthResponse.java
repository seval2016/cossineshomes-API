package com.project.payload.response.authentication;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.project.payload.response.business.AdvertResponse;
import com.project.payload.response.business.TourRequestResponse;
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

    private Boolean builtIn = false;

    private Set<String> userRole;

    private String token;


    private Set<AdvertResponse> advert;

    private Set<TourRequestResponse>tourRequestsResponse;

    private List<Long> favoritesList;
}
