package com.project.service.helper;

import com.project.entity.concretes.business.*;
import com.project.entity.concretes.user.User;
import com.project.entity.enums.AdvertStatus;
import com.project.exception.BadRequestException;
import com.project.exception.ResourceNotFoundException;
import com.project.payload.mappers.AdvertMapper;
import com.project.payload.messages.ErrorMessages;
import com.project.payload.request.business.AdvertRequest;

import com.project.payload.response.business.image.ImageResponse;
import com.project.repository.business.AdvertRepository;
import com.project.service.business.*;
import com.project.service.validator.DateTimeValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
@RequiredArgsConstructor
public class AdvertHelper {

    private final AdvertRepository advertRepository;
    private final CategoryService categoryService;
    private final MethodHelper methodHelper;
    private final CityService cityService;
    private final CountryService countryService;
    private final AdvertTypesService advertTypesService;
    private final DateTimeValidator dateTimeValidator;
    private final DistrictService districtService;
    private final CategoryHelper categoryHelper;


    /**
     * İlanın popülarite puanını hesaplar.
     */
    private int calculatePopularityPoint(Advert advert) {
        int totalTourRequests = advert.getTourRequestList().size();
        int totalViews = advert.getViewCount();
        return (3 * totalTourRequests) + totalViews;
    }

    /**
     * İlan detaylarını alır ve gerekli bilgileri harita olarak döner.
     */
    public Map<String, Object> getAdvertDetails(AdvertRequest advertRequest, HttpServletRequest httpServletRequest, Map<String, Object> detailsMap) {
        if (detailsMap == null) {
            detailsMap = new HashMap<>();
        }
        Category category = categoryService.getCategoryById(advertRequest.getCategoryId());
        City city = cityService.getCityById(advertRequest.getCityId());
        User user = methodHelper.getUserByHttpRequest(httpServletRequest);
        Country country = countryService.getCountryById(advertRequest.getCountryId());
        AdvertType advertType = advertTypesService.getAdvertTypeByIdForAdvert(advertRequest.getAdvertTypeId());
        District district = districtService.getDistrictById(advertRequest.getDistrictId());

        detailsMap.put("category", category);
        detailsMap.put("city", city);
        detailsMap.put("user", user);
        detailsMap.put("country", country);
        detailsMap.put("advertType", advertType);
        detailsMap.put("district", district);

        return detailsMap;
    }

    /**
     * Belirtilen ID'ye sahip ilanı kontrol eder ve var ise döner, yoksa hata fırlatır.
     */
    public Advert isAdvertExistById(Long id) {
        return advertRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(String.format(ErrorMessages.NOT_FOUND_ADVERT_WITH_ID_MESSAGE, id))
        );
    }

    /**
     * Belirtilen tarih aralığı, kategori, tür ve durum bilgileri ile ilan raporu döner.
     */
    public List<Advert> getAdvertsReport(String date1, String date2, String category, String type, AdvertStatus status) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        LocalDateTime begin = LocalDateTime.parse(date1, formatter);
        LocalDateTime end = LocalDateTime.parse(date2, formatter);
        dateTimeValidator.checkBeginTimeAndEndTime(begin, end);

        categoryHelper.getCategoryByTitle(category);
        AdvertStatus statusEnum = AdvertStatus.fromValue(status.getValue());
        advertTypesService.findByTitle(type);

        return advertRepository.findByQuery(begin, end, category, type, statusEnum).orElseThrow(
                () -> new BadRequestException(ErrorMessages.ADVERT_NOT_FOUND)
        );
    }

    /**
     * Tüm ilanları döner.
     */
    public List<Advert> getAllAdverts() {
        return advertRepository.findAll();
    }

    /**
     * Verilen ilanı veritabanına kaydeder.
     */
    public void saveRunner(Advert advert) {
        advertRepository.save(advert);
    }

    /**
     * Favoriler için belirtilen ID'ye sahip ilanı döner.
     */
    public Advert getAdvertForFavorites(Long id) {
        return advertRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.ADVERT_NOT_FOUND));
    }

    /**
     * Advert entity'sini ID ile getirir.
     */
    public Advert getAdvertById(Long id) {
        return advertRepository.findById(id).orElse(null);
    }

    /**
     * Featured image'ı getirir.
     */

    @Transactional
    public Page<Advert> getPopularAdverts(int amount, Pageable pageable) {

        return advertRepository.getMostPopularAdverts(amount,pageable);

    }
}
