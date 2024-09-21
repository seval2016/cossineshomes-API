package com.project.service.business;

import com.project.entity.concretes.business.*;
import com.project.entity.concretes.business.Image;
import com.project.entity.concretes.user.User;
import com.project.entity.enums.LogEnum;
import com.project.entity.enums.RoleType;
import com.project.exception.ResourceNotFoundException;
import com.project.payload.mappers.AdvertMapper;
import com.project.payload.messages.ErrorMessages;
import com.project.payload.messages.SuccessMessages;
import com.project.payload.request.business.AdvertRequest;
import com.project.payload.request.business.CreateAdvertPropertyRequest;
import com.project.payload.response.business.*;

import com.project.payload.response.business.advert.*;
import com.project.payload.response.business.category.CategoryAdvertResponse;
import com.project.payload.response.business.image.ImageResponse;
import com.project.repository.business.*;
import com.project.service.helper.AdvertHelper;
import com.project.service.helper.CategoryPropertyKeyHelper;
import com.project.service.helper.MethodHelper;
import com.project.service.helper.PageableHelper;

import com.project.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Lazy
@RequiredArgsConstructor
public class AdvertService {

    private final PageableHelper pageableHelper;
    private final AdvertRepository advertRepository;
    private final AdvertMapper advertMapper;
    private final MethodHelper methodHelper;
    private final CategoryPropertyValueService categoryPropertyValueService;
    private final CategoryPropertyKeyHelper categoryPropertyKeyHelper;
    private final LogService logService;
    private final AdvertHelper advertHelper;

    //A01
    public Page<AdvertListResponse> getAdvertsByPage(String query, Long categoryId, Long advertTypeId,BigDecimal priceStart, BigDecimal priceEnd,Integer status, Pageable pageable) {

        Page<Advert> advertsPage = advertRepository.findByAdvertByPage(query, categoryId, advertTypeId, priceStart, priceEnd,status, pageable);
        return advertsPage.map(advertMapper::toAdvertListResponse);
    }

     //A02
    public List<CityAdvertResponse> getAdvertsGroupedByCities() {
        return advertRepository.findAdvertsGroupedByCities();
    }

    //A03
    public List<CategoryAdvertResponse> getAdvertsGroupedByCategory() {
        return advertRepository.findAdvertsGroupedByCategories();
    }

    //A04
    public List<PopularAdvertResponse> getPopularAdverts(Integer amount) {
        if (amount == null || amount <= 0) {
            amount = 10;
        }
        Pageable pageable = PageRequest.of(0, amount);
        List<PopularAdvertResponse> popularAdverts = advertRepository.findPopularAdverts(pageable);

        // Her PopularAdvertResponse için featuredImage'ı set et
        return popularAdverts.stream()
                .map(response -> {
                    Advert advert = advertHelper.getAdvertById(response.getId());
                    ImageResponse imageResponse = advertHelper.getFeaturedImage(advert);
                    return response.toBuilder()
                            .featuredImage(imageResponse)
                            .build();
                })
                .collect(Collectors.toList());
    }

    //A05
    public Page<AdvertResponseForCustomer> getAllAdvertForAuthUser(HttpServletRequest httpServletRequest, int page, int size, String sort, String type) {
        // Sayfalama ve sıralama bilgilerini içeren Pageable nesnesi oluşturuluyor
        Pageable pageable = pageableHelper.getPageableWithProperties(page, size, sort, type);

        // HTTP isteğinden kullanıcı bilgileri alınır
        User user = methodHelper.getUserByHttpRequest(httpServletRequest);

        // Kullanıcının ilanlarını sayfalandırılmış şekilde alınır ve AdvertResponse'e dönüştürülür
        Page<Advert> advertsPage = advertRepository.findByUser(user, pageable);

        return advertsPage.map(advert ->
                new AdvertResponseForCustomer(
                        advert.getId(),
                        advert.getTitle(),
                        advert.getImages().isEmpty() ? null : advert.getImages().get(0).getUrl()
                )
        );
    }

    //A06 - Yönetici ve yöneticiler için ilanları belirli kriterlere göre filtreleyip sayfalı ve sıralı bir şekilde döndürür.
    public Page<AdvertResponse> getFilteredAdverts(String query, Long categoryId, Long advertTypeId, Double priceStart, Double priceEnd, Integer status, int page, int size, String sort, String type) {

        // Sort nesnesi oluşturulur
        Sort.Direction direction = Sort.Direction.fromString(type);
        Sort sortBy = Sort.by(direction, sort);

        // Pageable nesnesi oluşturulur
        Pageable pageable = PageRequest.of(page, size, sortBy);

        // Verilen kriterlere göre filtrelenmiş sayfa ilanları alınır
        Page<Advert> advertPage = advertRepository.findByAdvertByQuery(query, categoryId, advertTypeId, priceStart, priceEnd, status, pageable);

        // Advert entity'lerini AdvertResponse DTO'larına dönüştürür
        return advertPage.map(advertMapper::mapAdvertToAdvertResponse);
    }

    //A07
    public AdvertDetailsForSlugResponse getAdvertBySlug(String slug) {
        // Slug ile ilanı advertRepository üzerinden bulur.
        Advert advert = advertRepository.findBySlug(slug)
                .orElseThrow(() -> new EntityNotFoundException("Advert not found with slug: " + slug));

        return advertMapper.mapAdvertToAdvertResponseForSlug(advert);
    }

    //A08
    public AdvertResponseForUser getAdvertByIdForCustomer(Long id, HttpServletRequest request) {


        User user = methodHelper.getUserAndCheckRoles(request,RoleType.CUSTOMER.name());

        Advert advert=advertHelper.isAdvertExistById(id);
        if(advert.getUser().getId()!=user.getId()){
            throw new ResourceNotFoundException(String.format(ErrorMessages.ADVERT_IS_NOT_FOUND_FOR_USER,user.getId()));
        }
        return advertMapper.mapAdvertToAdvertResponseForUser(advert);
    }

    //A09
    public AdvertResponseForUser getAdvertByIdForAdmin(Long id, HttpServletRequest request) {

        User user = methodHelper.getUserAndCheckRoles(request,RoleType.ADMIN.name());

        Advert advert = methodHelper.isAdvertExistById(id);
        return advertMapper.mapAdvertToAdvertResponseForUser(advert);
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
        for (CreateAdvertPropertyRequest request1 : advertRequest.getProperties()) {

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
        List<Image> imageList = methodHelper.getImagesForAdvert(files, savedAdvert.getImages());
        for (Image image : imageList) {
            image.setAdvert(savedAdvert); // Her bir resme ilgili ilanı setliyoruz
        }
        savedAdvert.setImages(imageList); // Resim listesini ilana ekliyoruz

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

        for (CreateAdvertPropertyRequest request1 : advertRequest.getProperties()) {
            List<CategoryPropertyValue> values = categoryPropertyValueService.categoryFindAllByValue(request1.getValue());
            advertValueList.addAll(values);
        }

        List<Image> imageList = methodHelper.getImagesForAdvert(files, advert != null ? advert.getImages() : null);
        if (imageList != null && advert != null) {
            for (Image image : imageList) {
                if (image != null) {
                    image.setAdvert(advert);
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
        updateAdvert.setImages(advert.getImages());

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
        for (CreateAdvertPropertyRequest request1 : advertRequest.getProperties()) {
            List<CategoryPropertyValue> values = categoryPropertyValueService.categoryFindAllByValue(request1.getValue());
            advertValueList.addAll(values);
        }

        // Yeni resimler eklenir, mevcut resimler güncellenir
        List<Image> imageList = methodHelper.getImagesForAdvert(files, advert.getImages());
        if (imageList != null && advert != null) {
            for (Image image : imageList) {
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
        updatedAdvert.setImages(imageList); // Resim listesi set edilir

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

        return advertMapper.mapAdvertToAdvertResponse(advert);
    }

}
