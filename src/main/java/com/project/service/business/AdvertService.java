package com.project.service.business;

import com.project.entity.concretes.business.*;

import com.project.entity.concretes.user.User;
import com.project.payload.mappers.AdvertMapper;
import com.project.payload.request.business.AdvertRequest;
import com.project.payload.response.business.AdvertResponse;

import com.project.payload.response.business.CityAdvertResponse;
import com.project.repository.business.*;
import com.project.repository.user.UserRepository;
import com.project.service.helper.PageableHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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

    public Page<AdvertResponse> getAdverts(String q, Long categoryId, Long advertTypeId, BigDecimal priceStart, BigDecimal priceEnd, Integer status, int page, int size, String sort, String type) {

        Pageable pageable=pageableHelper.getPageableWithProperties(q,categoryId,advertTypeId,priceStart,priceEnd,status,page,size,sort,type);

        return advertRepository.findAdverts(q, categoryId, advertTypeId, priceStart, priceEnd, status, pageable)
                .map(advertMapper::mapAdvertToAdvertResponse);

    }

    public AdvertResponse createAdvert(AdvertRequest advertRequest) {
        AdvertType advertType = advertTypeRepository.findById(advertRequest.getAdvertTypeId()).orElseThrow();
        Country country = countryRepository.findById(advertRequest.getCountryId()).orElseThrow();
        City city = cityRepository.findById(advertRequest.getCityId()).orElseThrow();
        District district = districtRepository.findById(advertRequest.getDistrictId()).orElseThrow();
        Category category = categoryRepository.findById(advertRequest.getCategoryId()).orElseThrow();
        User user = userRepository.findById(advertRequest.getUserId()).orElseThrow();

        Advert advert = advertMapper.mapAdvertRequestToAdvert(advertRequest, advertType, country, city, district, category, user);
        Advert savedAdvert = advertRepository.save(advert);

        return advertMapper.mapAdvertToAdvertResponse(savedAdvert);
    }

    public List<CityAdvertResponse> getAdvertCities() {
        return advertRepository.findAdvertsGroupedByCity();
    }
}