package com.project.controller.business;

import com.project.entity.concretes.business.Advert;
import com.project.entity.concretes.business.Favorite;
import com.project.payload.response.business.AdvertResponse;
import com.project.service.business.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    // Authenticated user's favorites
    @GetMapping("/auth")
    @PreAuthorize("hasRole('CUSTOMER')") //http://localhost:8080/favorites
    public ResponseEntity<List<AdvertResponse>> getAuthenticatedUserFavorites(HttpServletRequest request) {
        return ResponseEntity.ok(favoriteService.getAuthenticatedUserFavorites(request));
    }//K01

    @GetMapping("/admin/{id}") //kullanıcı favorilerini getiren kod
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')") //http://localhost:8080/favorites/admin/3
    public ResponseEntity<List<AdvertResponse>> getUserFavorites(@PathVariable Long id) {
        List<Advert> favorites = favoriteService.getUserFavorites(id);
        List<AdvertResponse> response = favorites.stream()
                .map(advert -> new AdvertResponse(advert.getId(), advert.getTitle())) // Gereken alanları belirleyin
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }//K02

    @PostMapping("/{id}/auth")
    @PreAuthorize("hasRole('CUSTOMER')")//http://localhost:8080/favorites/12/auth
    public ResponseEntity<AdvertResponse> addOrRemoveFavorite(@PathVariable Long id,HttpServletRequest request) {
        return ResponseEntity.ok(favoriteService.addOrRemoveFavorite(id,request));
    }//K03

    @DeleteMapping("/auth")
    @PreAuthorize("hasRole('CUSTOMER')")//http://localhost:8080/favorites/admin
    public ResponseEntity<String> deleteAllFavorites(HttpServletRequest request) {
        String resultMessage = favoriteService.deleteAllFavorites(request);
        return new ResponseEntity<>(resultMessage, HttpStatus.OK);
    }//K04

    @DeleteMapping("/admin")//http://localhost:8080/favorites/admin
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<?> deleteAllFavoritesOfUser(@RequestParam("userId") long userId) {
        try {
            favoriteService.deleteAllFavoritesByUserId(userId);
            return ResponseEntity.ok("All favorites for user with ID " + userId + " have been removed.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while deleting favorites for user with ID " + userId);
        }
    }//K05

    @DeleteMapping("/{id}/admin")//http://localhost:8080/favorites/12/admin
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")//id numarasına sahip bir favori kaydı silmek için kullanılır.
    public ResponseEntity<String> deleteFavoriteByAdmin(@PathVariable("id") Long id) {
        return ResponseEntity.ok(favoriteService.deleteFavoriteByAdmin(id));
    }//K06
}
