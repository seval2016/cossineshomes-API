package com.project.service.business;

import com.project.entity.concretes.business.*;
import com.project.entity.concretes.user.User;
import com.project.entity.enums.AdvertStatus;
import com.project.entity.enums.LogEnum;
import com.project.entity.enums.RoleType;
import com.project.exception.BadRequestException;
import com.project.exception.ResourceNotFoundException;
import com.project.payload.mappers.AdvertMapper;
import com.project.payload.messages.ErrorMessages;
import com.project.payload.messages.SuccessMessages;
import com.project.payload.request.business.AdvertRequest;
import com.project.payload.request.business.PropertyRequest;
import com.project.payload.response.business.*;

import com.project.repository.business.*;
import com.project.repository.user.UserRepository;
import com.project.service.helper.MethodHelper;
import com.project.service.helper.PageableHelper;

import com.project.service.validator.DateTimeValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdvertService {

    private final AdvertRepository advertRepository;
    private final AdvertMapper advertMapper;
    private final PageableHelper pageableHelper;
    private final AdvertTypesRepository advertTypesRepository;
    private final CountryRepository countryRepository;
    private final CityRepository cityRepository;
    private final DistrictRepository districtRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final MethodHelper methodHelper;
    private final ImagesRepository imagesRepository;
    private final CategoryPropertyValueService categoryPropertyValueService;
    private final LogService logService;
    private final DateTimeValidator dateTimeValidator;
    private final CategoryService categoryService;
    private final AdvertTypesService advertTypesService;
    private final AdvertStatus advertStatus;


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
        AdvertType advertType = advertTypesRepository.findById(advertRequest.getAdvertTypeId()).orElseThrow();
        Country country = countryRepository.findById(advertRequest.getCountryId()).orElseThrow();
        City city = cityRepository.findById(advertRequest.getCityId()).orElseThrow();
        District district = districtRepository.findById(advertRequest.getDistrictId()).orElseThrow();
        Category category = categoryRepository.findById(advertRequest.getCategoryId()).orElseThrow();

        // imageIds listesinden resimleri bulup setliyoruz
        List<Images> images = imagesRepository.findAllById(advertRequest.getImageIds());

        Advert advert = advertMapper.mapAdvertRequestToAdvert(advertRequest, advertType, country, city, district, category, user, images);
        Advert savedAdvert = advertRepository.save(advert);

        return advertMapper.mapAdvertToAdvertResponse(savedAdvert);
    }

    public List<CategoryAdvertResponse> getAdvertsGroupedByCategory() {
        return advertRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(advert -> advert.getCategory().getTitle(), Collectors.counting()))
                .entrySet()
                .stream()
                .map(entry -> new CategoryAdvertResponse(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    public Page<AdvertResponse> getAuthenticatedUserAdverts(int page, int size, String sortField, String sortType) {
        return null;
    }

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

    public Page<AdvertResponse> getFilteredAdverts(String q, Long categoryId, Long advertTypeId, Double priceStart, Double priceEnd, Integer status, int page, int size, String sort, String type) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(type), sort));
        Page<Advert> advertPage = advertRepository.findAdvertsByCriteria(q, categoryId, advertTypeId, priceStart, priceEnd, status, pageable);

        // Map Advert entities to AdvertResponse DTOs
        return advertPage.map(advertMapper::mapAdvertToAdvertResponse);
    }

    public AdvertResponse getAdvertBySlug(String slug) {
        Advert advert = advertRepository.findBySlug(slug)
                .orElseThrow(() -> new EntityNotFoundException("Advert not found with slug: " + slug));

        // Convert the Advert entity to AdvertResponse DTO
        return advertMapper.mapAdvertToAdvertResponse(advert);
    }

    public AdvertResponse getAdvertByIdAndAuthenticatedUser(Long id, HttpServletRequest request) {
        String userName = (String) request.getAttribute("username");
        User user = userRepository.findByUsernameEquals(userName);

        Advert advert = advertRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Advert not found or you're not the owner"));

        return advertMapper.mapAdvertToAdvertResponseForAll(advert);
    }

    public ResponseEntity<AdvertResponse> getAdvertById(Long id, HttpServletRequest request) {

        User user = methodHelper.getUserAndCheckRoles(request, RoleType.CUSTOMER.name());

        Advert advert = methodHelper.isAdvertExistById(id);
        if (advert.getUser().getId() != user.getId()) {
            throw new ResourceNotFoundException(String.format(ErrorMessages.ADVERT_IS_NOT_FOUND_FOR_USER, user.getId()));
        }

        return ResponseEntity.ok(advertMapper.mapAdvertToAdvertResponse(advert));
    }

    public Page<AdvertResponse> getAllAdvertForAuthUser(HttpServletRequest httpServletRequest, int page, int size, String sort, String type) {

        Pageable pageable = pageableHelper.getPageableWithProperties(page, size, sort, type);

        User user = methodHelper.getUserByHttpRequest(httpServletRequest);

        return advertRepository.findAdvertsForUser(user.getId(), pageable).map(
                (advert) -> {
                    AdvertResponse response = advertMapper.mapAdvertToAdvertResponse(advert);
                    response.setFavoritesCount(advert.getFavoritesList().size());
                    return response;
                });
    }

    public ResponseMessage<AdvertResponse> updateAuthenticatedAdvert(AdvertRequest advertRequest, MultipartFile[] files, HttpServletRequest httpServletRequest, Long id) {
        User user = methodHelper.getUserAndCheckRoles(httpServletRequest, RoleType.CUSTOMER.name());
        Advert advert = methodHelper.isAdvertExistById(id);

        if (advert.isBuiltIn()) {
            throw new ResourceNotFoundException(ErrorMessages.THIS_ADVERT_DOES_NOT_UPDATE);
        }
        if (!advert.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException(ErrorMessages.ADVERT_NOT_FOUND);
        }

        Map<String, Object> detailsMap = new HashMap<>();
        methodHelper.getAdvertDetails(advertRequest, httpServletRequest, detailsMap);

        List<CategoryPropertyValue> advertValueList = new ArrayList<>();

        for (PropertyRequest request1 : advertRequest.getProperties()) {
            List<CategoryPropertyValue> values = categoryPropertyValueService.categoryFindAllByValue(request1.getValue());
            advertValueList.addAll(values);
        }

        List<Images> imagesList = methodHelper.getImagesForAdvert(files, advert != null ? advert.getImagesList() : null);
        if (imagesList != null && advert != null) {
            for (Images images : imagesList) {
                if (images != null) {
                    images.setAdvert(advert);
                }
            }
        }

        Advert updateAdvert = advertMapper.mapAdvertRequestToUpdateAdvert(id, advertRequest,
                (Category) detailsMap.get("category"),
                (City) detailsMap.get("city"),
                (Country) detailsMap.get("country"),
                (AdvertType) detailsMap.get("advertType"),
                (District) detailsMap.get("district"),
                (User) detailsMap.get("user"));
        updateAdvert.setCategoryPropertyValuesList(advertValueList);

        if (updateAdvert.getCreateAt() == null) {
            updateAdvert.setCreateAt(LocalDateTime.now());
        }
        updateAdvert.setUpdateAt(LocalDateTime.now());

        updateAdvert.isActive();
        updateAdvert.setSlug(advert.getSlug());
        updateAdvert.setImagesList(advert.getImagesList());

        Advert returnedAdvert = advertRepository.save(updateAdvert);

        logService.createLogEvent(advert.getUser(), advert, LogEnum.UPDATED);

        return ResponseMessage.<AdvertResponse>builder()
                .message(SuccessMessages.ADVERT_UPDATED)
                .object(advertMapper.mapAdvertToAdvertResponse(returnedAdvert))
                .httpStatus(HttpStatus.OK)
                .build();
    }

    public ResponseMessage<AdvertResponse> updateAdvert(AdvertRequest advertRequest, MultipartFile[] files, HttpServletRequest httpServletRequest, Long id) {
        User user = methodHelper.getUserByHttpRequest(httpServletRequest);
        methodHelper.checkRoles(user, RoleType.ADMIN, RoleType.MANAGER); // Admin veya Manager rolü kontrol ediliyor
        Advert advert = methodHelper.isAdvertExistById(id); // İlanın var olup olmadığı kontrol ediliyor

        if (advert.isBuiltIn()) {
            throw new ResourceNotFoundException(ErrorMessages.THIS_ADVERT_DOES_NOT_UPDATE); // Eğer ilan built-in ise güncellenemez
        }

        // İlan detayları haritalanıyor
        Map<String, Object> detailsMap = methodHelper.getAdvertDetails(advertRequest, httpServletRequest, null);

        // Kategoriye bağlı property'ler alınır ve ilan ile ilişkilendirilir
        List<CategoryPropertyValue> advertValueList = new ArrayList<>();
        for (PropertyRequest request1 : advertRequest.getProperties()) {
            List<CategoryPropertyValue> values = categoryPropertyValueService.categoryFindAllByValue(request1.getValue());
            advertValueList.addAll(values);
        }

        // Yeni resimler eklenir, mevcut resimler güncellenir
        List<Images> imagesList = methodHelper.getImagesForAdvert(files, advert.getImagesList());
        if (imagesList != null && advert != null) {
            for (Images image : imagesList) {
                image.setAdvert(advert); // Resimler ilana eklenir
            }
        }

        // İlanın güncellenmiş hali haritalanır
        Advert updatedAdvert = advertMapper.mapAdvertRequestToUpdateAdvert(id, advertRequest,
                (Category) detailsMap.get("category"),
                (City) detailsMap.get("city"),
                (Country) detailsMap.get("country"),
                (AdvertType) detailsMap.get("advertType"),
                (District) detailsMap.get("district"),
                user);

        // Property değerleri ve diğer alanlar güncellenir
        updatedAdvert.setCategoryPropertyValuesList(advertValueList);
        updatedAdvert.setUpdateAt(LocalDateTime.now()); // Güncellenme zamanı set edilir
        updatedAdvert.setImagesList(imagesList); // Resim listesi set edilir

        // Güncellenen ilan kaydedilir
        Advert savedAdvert = advertRepository.save(updatedAdvert);

        // Güncelleme olayı loglanır
        logService.createLogEvent(user, savedAdvert, LogEnum.UPDATED);

        // Başarı mesajı ve güncellenen ilan dönülür
        return ResponseMessage.<AdvertResponse>builder()
                .message(SuccessMessages.ADVERT_UPDATED)
                .object(advertMapper.mapAdvertToAdvertResponse(savedAdvert))
                .httpStatus(HttpStatus.OK)
                .build();
    }

    public AdvertResponse deleteAdvert(Long id, HttpServletRequest request) {
        User user = methodHelper.getUserByHttpRequest(request);
        methodHelper.checkRoles(user, RoleType.ADMIN, RoleType.MANAGER);
        Advert advert = methodHelper.isAdvertExistById(id);

        if (advert.isBuiltIn()) {
            throw new ResourceNotFoundException(ErrorMessages.THIS_ADVERT_DOES_NOT_UPDATE);
        }
        advertRepository.deleteById(id);

        //logService.createLogEvent(advert.getUser(),advert, LogEnum.DELETED);

        return advertMapper.mapAdvertToAdvertResponse(advert);
    }

    /*********************/

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

}
