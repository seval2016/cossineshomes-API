package com.project.payload.response.abstracts;

import com.project.payload.response.business.AdvertResponse;
import com.project.payload.response.business.TourRequestResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public abstract class BaseUserResponse {

    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String phone;

    private LocalDateTime createAt;

    private LocalDateTime updateAt;

    private Set<String> userRole;

    private Set<AdvertResponse> advert;

    private Set<TourRequestResponse> tourRequestsResponse;
    private Set<Long> favoritesList;
}
