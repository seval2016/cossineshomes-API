package com.project.service.business;

import com.project.repository.business.*;
import com.project.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResetService {

    private final AdvertRepository advertRepository;
    private final ImagesRepository imagesRepository;
    private final CategoryRepository categoryRepository;
    private final AdvertTypesRepository advertTypesRepository;
    private final CategoryPropertyKeyRepository categoryPropertyKeyRepository;
    private final CategoryPropertyValueRepository categoryPropertyValueRepository;
    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final TourRequestRepository tourRequestRepository;

    public void resetDatabase() {
        // Favorites tablosundaki verileri sil (builtIn=true olanlar hariç)
        favoriteRepository.deleteAllFavoritesExceptBuiltIn();

        // Kullanıcıları sil (builtIn=true olanlar hariç)
        userRepository.deleteAllUsersExceptBuiltIn();

        // Advert tablosundaki verileri sil (builtIn=true olanlar hariç)
        advertRepository.deleteAllAdvertsExceptBuiltIn();


        // Category tablosundaki verileri sil (builtIn=true olanlar hariç)
        categoryRepository.deleteAllCategoriesExceptBuiltIn();

        // AdvertType tablosundaki verileri sil (builtIn=true olanlar hariç)
        advertTypesRepository.deleteAllAdvertTypesExceptBuiltIn();

        // CategoryPropertyKey tablosundaki verileri sil (builtIn=true olanlar hariç)
        categoryPropertyKeyRepository.deleteAllCategoryPropertyKeysExceptBuiltIn();

        // CategoryPropertyValue tablosundaki verileri sil (builtIn=true olanlar hariç)
        categoryPropertyValueRepository.deleteAllCategoryPropertyValuesExceptBuiltIn();

        // TourRequest tablosundaki verileri sil (builtIn=true olanlar hariç)
        tourRequestRepository.deleteAllTourRequestsExceptBuiltIn();
    }
}
