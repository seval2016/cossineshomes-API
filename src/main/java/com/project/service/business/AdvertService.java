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
import com.project.service.helper.AdvertHelper;
import com.project.service.helper.CategoryPropertyKeyHelper;
import com.project.service.helper.MethodHelper;
import com.project.service.helper.PageableHelper;

import com.project.service.validator.DateTimeValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
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
@Lazy
@RequiredArgsConstructor
public class AdvertService {

    private final PageableHelper pageableHelper;
    private final AdvertRepository advertRepository;
    private final AdvertMapper advertMapper;
    private final CategoryService categoryService;
    private final MethodHelper methodHelper;
    private final CategoryPropertyValueService categoryPropertyValueService;
    private final CategoryPropertyKeyHelper categoryPropertyKeyHelper;
    private final LogService logService;
    private final AdvertHelper advertHelper;


    // --> A01 - Belirli filtreleme kriterlerine göre ilanları getirme işlemleri.
    public Page<AdvertResponse> getAdverts(String query, Long categoryId, Long advertTypeId, BigDecimal priceStart, BigDecimal priceEnd, Integer status, int page, int size, String sort, String type) {
        Pageable pageable = pageableHelper.getPageableWithProperties(query, categoryId, advertTypeId, priceStart, priceEnd, status, page, size, sort, type);
        return advertRepository.findAdverts(query, categoryId, advertTypeId, priceStart, priceEnd, status, pageable)
                .map(advertMapper::mapAdvertToAdvertResponse);
    }

    public List<CityAdvertResponse> getAdvertsGroupedByCities() {
        return advertRepository.findAdvertsGroupedByCities();
    } //A02

    //A03
    public List<CategoryForAdvertResponse> getAdvertsGroupedByCategory() {
        List<Category> categoryList = categoryService.getAllCategory();

        List<CategoryForAdvertResponse> categoryForAdvertList= categoryList.stream().map(advertMapper::mapCategoryToCategoryForAdvertResponse).toList();

        return categoryForAdvertList;
    }

    //A04
    public Page<AdvertResponse> getAuthenticatedUserAdverts(int page, int size, String sortField, String sortType) {
        return null;
    }

    //A05
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

    //A06
    public Page<AdvertResponse> getFilteredAdverts(String q, Long categoryId, Long advertTypeId, Double priceStart, Double priceEnd, Integer status, int page, int size, String sort, String type) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(type), sort));
        Page<Advert> advertPage = advertRepository.findAdvertsByCriteria(q, categoryId, advertTypeId, priceStart, priceEnd, status, pageable);

        // Map Advert entities to AdvertResponse DTOs
        return advertPage.map(advertMapper::mapAdvertToAdvertResponse);
    }

    //A07
    public AdvertResponse getAdvertBySlug(String slug) {
        Advert advert = advertRepository.findBySlug(slug)
                .orElseThrow(() -> new EntityNotFoundException("Advert not found with slug: " + slug));

        // Convert the Advert entity to AdvertResponse DTO
        return advertMapper.mapAdvertToAdvertResponse(advert);
    }

    //A08
    public AdvertResponse getAdvertByIdAndAuthenticatedUser(Long id, HttpServletRequest request) {
        User user = methodHelper.getUserAndCheckRoles(request,RoleType.ADMIN.roleName);
        Advert advert=advertHelper.isAdvertExistById(id);
        if (advert.getUser().getId() != user.getId()) {
            throw new ResourceNotFoundException(String.format(ErrorMessages.ADVERT_IS_NOT_FOUND_FOR_USER, user.getId()));
        }
        return advertMapper.mapAdvertToAdvertResponseForAll(advert);
    }

    //A09
    public AdvertResponse getAdvertById(Long id, HttpServletRequest request) {

        User user = methodHelper.getUserAndCheckRoles(request, RoleType.CUSTOMER.name());

        Advert advert = methodHelper.isAdvertExistById(id);
        return advertMapper.mapAdvertToAdvertResponse(advert);
    }

    //A10
    public AdvertResponse createAdvert(AdvertRequest advertRequest, HttpServletRequest request,MultipartFile[] files) {

        // İlanın detaylarını tutacak bir map oluşturuyoruz
        Map<String, Object> detailsMap = new HashMap<>();

        // İlanla ilgili detayları alıyoruz (kullanıcı, kategori, şehir, ülke gibi) ve detailsMap içine ekliyoruz
        advertHelper.getAdvertDetails(advertRequest, request, detailsMap);

        // AdvertRequest'ten gelen verileri alıp bir Advert nesnesi oluşturuyoruz ve ilişkisel verileri setliyoruz
        Advert advert = advertMapper.mapAdvertRequestToAdvert(
                advertRequest,
                (Category) detailsMap.get("category"),
                (City) detailsMap.get("city"),
                (User) detailsMap.get("user"),
                (Country) detailsMap.get("country"),
                (AdvertType) detailsMap.get("advertType"),
                (District) detailsMap.get("district"));

        // İlanın özellik değerlerini (property values) tutacak bir liste oluşturuyoruz
        List<CategoryPropertyValue> advertValueList = new ArrayList<>();

        // Gelen özellik isteklerini döngü ile işliyoruz
        for (PropertyRequest request1 : advertRequest.getProperties()) {

            // Veritabanından bu keyId'ye karşılık gelen CategoryPropertyKey'i buluyoruz
            CategoryPropertyKey categoryPropertyKeyFromDb = categoryPropertyKeyHelper.findPropertyKeyById(request1.getKeyId());

            if (categoryPropertyKeyFromDb != null) {
                // Eğer key bulunduysa, log mesajı yazıyoruz
                System.out.println("Found CategoryPropertyKey: " + categoryPropertyKeyFromDb.getId());

                // Yeni bir CategoryPropertyValue nesnesi oluşturuyoruz
                CategoryPropertyValue categoryPropertyValue = new CategoryPropertyValue();

                // İstekten gelen değeri (value) CategoryPropertyValue nesnesine setliyoruz
                categoryPropertyValue.setValue(request1.getValue());
                System.out.println("Setting value: " + request1.getValue());

                // Bulduğumuz CategoryPropertyKey'i CategoryPropertyValue nesnesine setliyoruz
                categoryPropertyValue.setCategoryPropertyKey(categoryPropertyKeyFromDb);

                // CategoryPropertyValue nesnesini kaydediyoruz ve tekrar veritabanından gelen nesneyi alıyoruz
                categoryPropertyValue = categoryPropertyValueService.saveCategoryPropertyValue(categoryPropertyValue);
                System.out.println("Saved CategoryPropertyValue with ID: " + categoryPropertyValue.getId());

                // Bu nesneyi advertValueList'e ekliyoruz
                advertValueList.add(categoryPropertyValue);
                System.out.println("Added to advertValueList");
            } else {
                // Eğer key bulunamazsa log mesajı yazıyoruz
                System.out.println("CategoryPropertyKey bulunamadı: ");
            }
        }

        // İlanın property value listesini oluşturduğumuz liste ile setliyoruz
        advert.setCategoryPropertyValuesList(advertValueList);

        // Eğer viewCount değeri null ise, varsayılan olarak 0 setliyoruz
        if (advert.getViewCount() == null) {
            advert.setViewCount(0); // Varsayılan değer olarak 0
        }

        // Eğer builtIn değeri null ise, varsayılan olarak false setliyoruz
        if (advert.getBuiltIn() == null) {
            advert.setBuiltIn(false);
        }

        // Eğer isActive değeri null ise, varsayılan olarak false setliyoruz
        if (advert.getIsActive() == null) {
            advert.setIsActive(false);
        }

        // İlanı veritabanına kaydediyoruz ve ID'sini alıyoruz
        Advert savedAdvert = advertRepository.save(advert);

        // Kaydedilen ilan için bir slug (SEO dostu URL) oluşturuyoruz
        savedAdvert.generateSlug();

        // Slug oluşturduktan sonra ilanı tekrar kaydediyoruz
        advertRepository.save(savedAdvert);

        // Resimleri işlemek için advert_id'yi ayarlıyoruz ve resimleri kaydediyoruz
        List<Images> imagesList = methodHelper.getImagesForAdvert(files, savedAdvert.getImagesList());
        for (Images image : imagesList) {
            image.setAdvert(savedAdvert); // Her bir resme ilgili ilanı setliyoruz
        }
        savedAdvert.setImagesList(imagesList); // Resim listesini ilana ekliyoruz

        // Log sistemi ile yeni bir ilan oluşturulduğunu kayıt altına alıyoruz
        logService.createLogEvent(savedAdvert.getUser(), savedAdvert, LogEnum.CREATED);

        // İlanı ve resimleri tekrar kaydediyoruz
        savedAdvert = advertRepository.save(savedAdvert);

        // Kaydedilen ilanı AdvertResponse'a dönüştürüp geri döndürüyoruz
        return advertMapper.mapAdvertToAdvertResponse(savedAdvert);
    }

    //A11
    public ResponseMessage<AdvertResponse> updateAuthenticatedAdvert(AdvertRequest advertRequest, MultipartFile[] files, HttpServletRequest httpServletRequest, Long id) {
        User user = methodHelper.getUserAndCheckRoles(httpServletRequest, RoleType.CUSTOMER.name());
        Advert advert = methodHelper.isAdvertExistById(id);

        if (advert.getBuiltIn()) {
            throw new ResourceNotFoundException(ErrorMessages.THIS_ADVERT_DOES_NOT_UPDATE);
        }
        if (!advert.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException(ErrorMessages.ADVERT_NOT_FOUND);
        }

        Map<String, Object> detailsMap = new HashMap<>();
        advertHelper.getAdvertDetails(advertRequest, httpServletRequest, detailsMap);

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

        updateAdvert.getIsActive();
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

     //A12
    public ResponseMessage<AdvertResponse> updateAdvert(AdvertRequest advertRequest, MultipartFile[] files, HttpServletRequest httpServletRequest, Long id) {
        User user = methodHelper.getUserByHttpRequest(httpServletRequest);
        methodHelper.checkRoles(user, RoleType.ADMIN, RoleType.MANAGER); // Admin veya Manager rolü kontrol ediliyor
        Advert advert = methodHelper.isAdvertExistById(id); // İlanın var olup olmadığı kontrol ediliyor

        if (advert.getBuiltIn()) {
            throw new ResourceNotFoundException(ErrorMessages.THIS_ADVERT_DOES_NOT_UPDATE); // Eğer ilan built-in ise güncellenemez
        }

        // İlan detayları haritalanıyor
        Map<String, Object> detailsMap =advertHelper.getAdvertDetails(advertRequest, httpServletRequest, null);

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

    //A13
    public AdvertResponse deleteAdvert(Long id, HttpServletRequest request) {
        User user = methodHelper.getUserByHttpRequest(request);
        methodHelper.checkRoles(user, RoleType.ADMIN, RoleType.MANAGER);
        Advert advert = methodHelper.isAdvertExistById(id);

        if (advert.getBuiltIn()) {
            throw new ResourceNotFoundException(ErrorMessages.THIS_ADVERT_DOES_NOT_UPDATE);
        }
        advertRepository.deleteById(id);

        //logService.createLogEvent(advert.getUser(),advert, LogEnum.DELETED);

        return advertMapper.mapAdvertToAdvertResponse(advert);
    }

}
