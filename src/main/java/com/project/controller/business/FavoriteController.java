package com.project.controller.business;

import com.project.payload.response.business.AdvertResponse;
import com.project.service.business.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    // Authenticated user's favorites
    @GetMapping("/auth")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<AdvertResponse>> getAuthenticatedUserFavorites(HttpServletRequest request) {
        return ResponseEntity.ok(favoriteService.getAuthenticatedUserFavorites(request));
    }
}
