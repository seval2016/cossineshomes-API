package com.project.payload.response.abstracts;

import com.project.payload.response.business.AdvertResponse;
import com.project.payload.response.business.TourRequestResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public abstract class BaseUserResponse {

    private Long userId;
    private String username;
    private String firstname;
    private String lastname;
    private String email;
    private String phone;
    private List<String> userRole;

    private List<AdvertResponse> advert;

    private List<TourRequestResponse> tourRequestsResponse;
    private List<Long> favoritesList;
}
