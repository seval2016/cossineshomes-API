package com.project.service.helper;

import com.project.entity.concretes.business.*;
import com.project.entity.concretes.user.User;
import com.project.entity.enums.AdvertStatus;
import com.project.exception.BadRequestException;
import com.project.exception.ResourceNotFoundException;
import com.project.payload.mappers.AdvertMapper;
import com.project.payload.messages.ErrorMessages;
import com.project.payload.request.business.AdvertRequest;
import com.project.payload.response.business.AdvertResponse;
import com.project.repository.business.AdvertRepository;
import com.project.service.business.*;
import com.project.service.validator.DateTimeValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AdvertHelper {

    private final AdvertRepository advertRepository;
    private final AdvertMapper advertMapper;
    private final CategoryService categoryService;
    private final MethodHelper methodHelper;
    private final CityService cityService;
    private final CountryService countryService;
    private final AdvertTypesService advertTypesService;
    private final DateTimeValidator dateTimeValidator;
    private final DistrictService districtService;
    private int calculatePopularityPoint(Advert advert) {
        int totalTourRequests = advert.getTourRequestList().size();
        int totalViews = advert.getViewCount();
        return (3 * totalTourRequests) + totalViews;
    }

    public List<AdvertResponse> getMostPopularAdverts(Pageable pageable) {
        // amount yerine Pageable kullanılıyor
        Page<Advert> popularAdverts = advertRepository.findMostPopularAdverts(pageable);
        return popularAdverts.stream()
                .map(advertMapper::mapAdvertToAdvertResponse)
                .collect(Collectors.toList());
    }

    public Map<String, Object> getAdvertDetails(AdvertRequest advertRequest, HttpServletRequest httpServletRequest, Map<String, Object> detailsMap) {
        if (detailsMap == null) {
            detailsMap = new HashMap<>();
        }
        Category category = categoryService.getCategoryById(advertRequest.getCategoryId());
        City city=cityService.getCityById(advertRequest.getCityId());
        User user = methodHelper.getUserByHttpRequest(httpServletRequest);
        Country country = countryService.getCountryById(advertRequest.getCountryId());
        AdvertType advertType = advertTypesService.getAdvertTypeByIdForAdvert(advertRequest.getAdvertTypeId());
        District district = districtService.getDistrictByIdForAdvert(advertRequest.getDistrictId());

        detailsMap.put("category", category);
        detailsMap.put("city", city);
        detailsMap.put("user", user);
        detailsMap.put("country", country);
        detailsMap.put("advertType", advertType);
        detailsMap.put("district", district);

        return detailsMap;
    }

    public Advert isAdvertExistById(Long id){
        return advertRepository.findById(id).orElseThrow(
                ()-> new ResourceNotFoundException(String.format(ErrorMessages.NOT_FOUND_ADVERT_WITH_ID_MESSAGE,id)));
    }

    public List<Advert> getAdvertsReport(String date1, String date2, String category, String type, AdvertStatus status) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        LocalDateTime begin = LocalDateTime.parse(date1,formatter);
        LocalDateTime end =LocalDateTime.parse(date2,formatter);
        dateTimeValidator.checkBeginTimeAndEndTime(begin,end);

        categoryService.getCategoryByTitle(category);

        AdvertStatus statusEnum = AdvertStatus.fromValue(status.getValue());

        advertTypesService.findByTitle(type);

        return advertRepository.findByQuery(begin,end,category,type,statusEnum).orElseThrow(
                ()-> new BadRequestException(ErrorMessages.ADVERT_NOT_FOUND)
        );

    }

    public List<Advert> getAllAdverts(){
        return advertRepository.findAll();
    }

    public void saveRunner(Advert advert) {
        advertRepository.save(advert);
    }

    public Advert getAdvertForFavorites(Long id){
        return advertRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException(ErrorMessages.ADVERT_NOT_FOUND));
    }

}
