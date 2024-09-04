package com.project.service.business;

import com.project.entity.concretes.business.*;
import com.project.entity.concretes.user.User;
import com.project.entity.enums.Status;
import com.project.exception.ResourceNotFoundException;
import com.project.payload.mappers.AdvertMapper;
import com.project.payload.messages.SuccessMessages;
import com.project.payload.request.business.AdvertRequest;
import com.project.payload.response.business.AdvertResponse;

import com.project.payload.response.business.CategoryAdvertResponse;
import com.project.payload.response.business.CityAdvertResponse;
import com.project.payload.response.business.ResponseMessage;
import com.project.repository.business.*;
import com.project.repository.user.UserRepository;
import com.project.service.helper.MethodHelper;
import com.project.service.helper.PageableHelper;
import com.project.service.validator.UniquePropertyValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdvertService {

    private final AdvertRepository advertRepository;
    private final AdvertMapper advertMapper;
    private final PageableHelper pageableHelper;
    private final AdvertTypeRepository advertTypeRepository;
    private final CountryRepository countryRepository;
    private final CityRepository cityRepository;
    private final DistrictRepository districtRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final MethodHelper methodHelper;
    private final UniquePropertyValidator uniquePropertyValidator;
    private final TourRequestRepository tourRequestRepository;
    private final FavoriteRepository favoriteRepository;
    private final ImageRepository imageRepository;
    private final LogRepository logRepository;


    //!!! 1. İlanları Getirme
    public Page<AdvertResponse> getAdverts(String q, Long categoryId, Long advertTypeId, BigDecimal priceStart, BigDecimal priceEnd, Integer status, int page, int size, String sort, String type) {
        Pageable pageable = pageableHelper.getPageableWithProperties(q, categoryId, advertTypeId, priceStart, priceEnd, status, page, size, sort, type);
        return advertRepository.findAdverts(q, categoryId, advertTypeId, priceStart, priceEnd, status, pageable)
                .map(advertMapper::mapAdvertToAdvertResponse);
    }

    //!!! 2. Yeni İlan Oluşturma
    public AdvertResponse createAdvert(AdvertRequest advertRequest, HttpServletRequest request) {
        String userName = (String) request.getAttribute("username");
        User user = userRepository.findByUsernameEquals(userName);

        //!!! İlgili tüm ilişkisel verileri (AdvertType, Country vs.) bulup setliyoruz
        AdvertType advertType = advertTypeRepository.findById(advertRequest.getAdvertTypeId()).orElseThrow();
        Country country = countryRepository.findById(advertRequest.getCountryId()).orElseThrow();
        City city = cityRepository.findById(advertRequest.getCityId()).orElseThrow();
        District district = districtRepository.findById(advertRequest.getDistrictId()).orElseThrow();
        Category category = categoryRepository.findById(advertRequest.getCategoryId()).orElseThrow();

        Advert advert = advertMapper.mapAdvertRequestToAdvert(advertRequest, advertType, country, city, district, category, user);
        Advert savedAdvert = advertRepository.save(advert);

        return advertMapper.mapAdvertToAdvertResponse(savedAdvert);
    }

    public List<CityAdvertResponse> getAdvertCities() {
        return advertRepository.findAdvertsGroupedByCity();
    }

    public List<CategoryAdvertResponse> getAdvertCategories() {
        return advertRepository.findAdvertsGroupedByCategory();
    }

    public ResponseMessage<AdvertResponse> updateAdvert(Long id, AdvertRequest advertRequest, HttpServletRequest request) {
        //!!! id var mi kontrolu :
        User user = methodHelper.isUserExist(id);

        // İlanı veri tabanından al
        Advert advert = advertRepository.findByIdAndUserId(id,user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("İlan bulunamadı veya bu kullanıcıya ait değil"));

        //!!! built_IN kontrolu
        methodHelper.checkBuiltIn(user);

        // İlanı güncellerken status alanını PENDING olarak değiştir
        advert.setStatus(Status.PENDING);

        // İlgili varlıkları veri tabanından getir
        AdvertType advertType = advertTypeRepository.findById(advertRequest.getAdvertTypeId()).orElseThrow();
        Country country = countryRepository.findById(advertRequest.getCountryId()).orElseThrow();
        City city = cityRepository.findById(advertRequest.getCityId()).orElseThrow();
        District district = districtRepository.findById(advertRequest.getDistrictId()).orElseThrow();
        Category category = categoryRepository.findById(advertRequest.getCategoryId()).orElseThrow();


        // İlanı güncelle
        advertMapper.mapAdvertRequestToAdvert(advertRequest, advertType, country, city, district, category, user);

        // Güncellenmiş ilanı kaydet
        Advert savedAdvert = advertRepository.save(advert);

        // Güncellenmiş ilanı response olarak döndür
        return ResponseMessage.<AdvertResponse>builder()
                .message(SuccessMessages.ADVERT_UPDATED)
                        .httpStatus(HttpStatus.OK)
                                .object( advertMapper.mapAdvertToAdvertResponse(savedAdvert))
                .build();
    }

    public AdvertResponse deleteAdvert(Long id) {

        Advert advert = advertRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("İlan bulunamadı"));

        if (advert.isBuiltIn()) {
            throw new IllegalStateException("Built-in ilanlar silinemez.");
        }

        // İlan ile ilişkili tüm verileri sil
        tourRequestRepository.deleteByAdvertId(id);
        favoriteRepository.deleteByAdvertId(id);
        logRepository.deleteByAdvertId(id);
        imageRepository.deleteByAdvertId(id);
        advertRepository.deleteById(id);;
        advertRepository.delete(advert);

        return advertMapper.mapAdvertToAdvertResponse(advert);
    }
}