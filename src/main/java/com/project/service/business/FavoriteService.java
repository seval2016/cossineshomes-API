package com.project.service.business;

import com.project.entity.concretes.business.Advert;
import com.project.entity.concretes.business.Favorite;
import com.project.entity.concretes.user.User;
import com.project.payload.mappers.AdvertMapper;
import com.project.payload.mappers.UserMapper;
import com.project.payload.messages.SuccessMessages;
import com.project.payload.response.abstracts.BaseUserResponse;
import com.project.payload.response.business.AdvertResponse;
import com.project.repository.business.AdvertRepository;
import com.project.repository.business.FavoriteRepository;
import com.project.repository.user.UserRepository;
import com.project.service.AuthenticationService;
import com.project.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteService {
    private final FavoriteRepository favoriteRepository;
    private final AdvertMapper advertMapper;
    private final AuthenticationService authenticationService;
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final AdvertRepository advertRepository;
    private final UserService userService;


    public List<AdvertResponse> getAuthenticatedUserFavorites(HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        BaseUserResponse authenticatedUser = authenticationService.findByUsername(username);

        List<Favorite> favorites = favoriteRepository.findByUser(userMapper.mapUserResponseToUser(authenticatedUser));

        // Convert Favorite entities to AdvertResponse DTOs
        return favorites.stream()
                .map(favorite -> advertMapper.mapAdvertToAdvertResponse(favorite.getAdvert()))
                .collect(Collectors.toList());
    }

    public List<Advert> getUserFavorites(Long userId) {
        return favoriteRepository.findFavoritesByUserId(userId);
    }

    public AdvertResponse addOrRemoveFavorite(Long advertId, HttpServletRequest request) {
        String userName = (String) request.getAttribute("username");
        User user = userRepository.findByUsernameEquals(userName);

        Advert advert = advertRepository.findById(advertId)
                .orElseThrow(() -> new EntityNotFoundException("Advert not found"));

        Favorite existingFavorite = favoriteRepository.findByUserAndAdvert(user, advert);

        if (existingFavorite != null) {
            // Eğer favorilerde varsa, favorilerden çıkar
            favoriteRepository.delete(existingFavorite);
        } else {
            // Eğer favorilerde yoksa, favorilere ekle
            Favorite favorite = new Favorite();
            favorite.setUser(user);
            favorite.setAdvert(advert);
            favorite.setCreateAt(LocalDateTime.now());
            favoriteRepository.save(favorite);
        }

        return advertMapper.mapAdvertToAdvertResponse(advert);
    }

    public String deleteAllFavorites(HttpServletRequest request) {

        String userName = (String) request.getAttribute("username");

        // Eğer kullanıcı adı null gelirse bir hata mesajı döndürün
        if (userName == null) {
            throw new RuntimeException("Kullanıcı adı bulunamadı.");
        }

        User user = userRepository.findByUsernameEquals(userName);

        if (user == null) {
            throw new RuntimeException("Kullanıcı bulunamadı.");
        }

        List<Favorite> userFavorites = favoriteRepository.findByUser(user);

        if (!userFavorites.isEmpty()) {
            favoriteRepository.deleteAll(userFavorites);
        }

        return SuccessMessages.ALL_FAVORITES_DELETED;
    }

       public String deleteAllFavoritesByUserId(long userId) {
             List<Favorite> favorites = favoriteRepository.findAllByUserId(userId);
             favoriteRepository.deleteAll(favorites);
             return SuccessMessages.ALL_FAVORITES_DELETED_BY_ID;
       }

    public String deleteFavoriteByAdmin(Long id) {
        Favorite favorite = favoriteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Favorite not found with id: " + id));

        favoriteRepository.delete(favorite);
        return SuccessMessages.FAVORITE_DELETED_BY_ID;
    }

}
