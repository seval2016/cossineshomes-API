package com.project.service.business;

import com.project.entity.concretes.business.Favorite;
import com.project.entity.concretes.user.User;
import com.project.payload.mappers.AdvertMapper;
import com.project.payload.mappers.UserMapper;
import com.project.payload.response.UserResponse;
import com.project.payload.response.abstracts.BaseUserResponse;
import com.project.payload.response.business.AdvertResponse;
import com.project.repository.business.FavoriteRepository;
import com.project.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteService {
    private final FavoriteRepository favoriteRepository;
    private final AdvertMapper advertMapper;
    private final AuthenticationService authenticationService;
    private final UserMapper userMapper;
    public List<AdvertResponse> getAuthenticatedUserFavorites(HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        BaseUserResponse authenticatedUser = authenticationService.findByUsername(username);

        List<Favorite> favorites = favoriteRepository.findByUser(userMapper.mapUserResponseToUser(authenticatedUser));

        // Convert Favorite entities to AdvertResponse DTOs
        return favorites.stream()
                .map(favorite -> advertMapper.mapAdvertToAdvertResponse(favorite.getAdvert()))
                .collect(Collectors.toList());
    }
}
