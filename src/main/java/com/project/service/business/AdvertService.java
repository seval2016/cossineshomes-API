package com.project.service.business;

import com.project.entity.concretes.business.*;
import com.project.entity.concretes.user.User;
import com.project.payload.mappers.AdvertMapper;
import com.project.payload.request.business.AdvertRequest;
import com.project.payload.response.business.*;

import com.project.repository.business.*;
import com.project.repository.user.UserRepository;
import com.project.service.helper.MethodHelper;
import com.project.service.helper.PageableHelper;
import com.project.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

import java.util.List;
import java.util.stream.Collectors;

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
     private final UserService userService;

    //!!! 1. İlanları Getirme
    public Page<AdvertResponse> getAdverts(String q, Long categoryId, Long advertTypeId, BigDecimal priceStart, BigDecimal priceEnd, Integer status, int page, int size, String sort, String type) {
        Pageable pageable = pageableHelper.getPageableWithProperties(q, categoryId, advertTypeId, priceStart, priceEnd, status, page, size, sort, type);
        return advertRepository.findAdverts(q, categoryId, advertTypeId, priceStart, priceEnd, status, pageable)
                .map(advertMapper::mapAdvertToAdvertResponse);
    }

    public List<CityAdvertResponse> getAdvertsGroupedByCities() {
        return advertRepository.findAdvertsGroupedByCities();
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

    public List<CategoryAdvertResponse> getAdvertsGroupedByCategory() {
        return advertRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(advert -> advert.getCategory().getTitle(), Collectors.counting()))
                .entrySet()
                .stream()
                .map(entry -> new CategoryAdvertResponse(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    private int calculatePopularityPoint(Advert advert) {
        int totalTourRequests = advert.getTourRequestList().size();
        int totalViews = advert.getViewCount();
        return (3 * totalTourRequests) + totalViews;
    }
    public List<AdvertResponse> getMostPopularAdverts(Integer amount) {
        // default olarak limit on tane ilan gösterilecek
        int limit = amount != null ? amount : 10;
        List<Advert> popularAdverts = advertRepository.findMostPopularAdverts(PageRequest.of(0, limit));
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

/*

public Page<AdvertResponse> getAuthenticatedUserAdverts(int page, int size, String sortField, String sortType) {
        User currentUser = userService.getAuthenticatedUser();  // Oturum açmış kullanıcıyı al

        Sort sort = Sort.by(Sort.Direction.fromString(sortType), sortField);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Advert> adverts = advertRepository.findByUser(currentUser, pageable);
        return adverts.map(advertMapper::mapAdvertToAdvertResponse);
    }
    public ResponseMessage<AdvertResponse> updateAuthenticatedAdvert(Long id, AdvertRequest advertRequest, HttpServletRequest request) {
        //!!! id var mi kontrolu :
        User user = methodHelper.isUserExist(id);

        // İlanı veri tabanından al
        Advert advert = advertRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("İlan bulunamadı veya bu kullanıcıya ait değil"));

        //!!! built_IN kontrolu
        methodHelper.checkBuiltIn(user);

        // İlanı güncellerken status alanını PENDING olarak değiştir
        advert.setStatus(AdvertStatus.PENDING.getValue());

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
                .object(advertMapper.mapAdvertToAdvertResponse(savedAdvert))
                .build();
    }

    public List<AdvertResponse> getPopularAdverts(int value) {

        List<Advert> advertList = advertRepository.findAll();

        return advertList.stream()
                .filter(t -> methodHelper.calculatePopularityPoint(t.getTourRequestList().size(), t.getViewCount()) >= value)
                .map(advertMapper::mapAdvertToAdvertResponse)
                .collect(Collectors.toList());
    }

    public AdvertResponse deleteAdvert(Long id, HttpServletRequest request) {
        User user = methodHelper.getUserByHttpRequest(request);
        methodHelper.checkRoles(user, RoleType.ADMIN, RoleType.MANAGER);
        Advert advert = isAdvertExistById(id);

        if (advert.isBuiltIn()) {
            throw new ResourceNotFoundException(ErrorMessages.THIS_ADVERT_DOES_NOT_UPDATE);
        }
        advertRepository.deleteById(id);

        return advertMapper.mapAdvertToAdvertResponse(advert);
    }

    public Advert isAdvertExistById(Long id) {
        return advertRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(String.format(ErrorMessages.NOT_FOUND_ADVERT_WITH_ID_MESSAGE, id)));
    }

    public Page<AdvertResponse> getAllAdvertForAuthUser(HttpServletRequest request, int page, int size, String sort, String type) {
        return null;
    }

    public ResponseMessage<AdvertResponse> updateAdvert(AdvertRequestForAdmin advertRequest, Long id, HttpServletRequest httpServletRequest, MultipartFile[] files) {
    return null;
    }
 */
}
