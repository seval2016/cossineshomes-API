package com.project.service.business;

import com.project.entity.concretes.business.Advert;
import com.project.entity.concretes.business.TourRequest;
import com.project.entity.concretes.user.User;
import com.project.entity.enums.Log;
import com.project.entity.enums.RoleType;
import com.project.entity.enums.TourRequestEnum;
import com.project.exception.BadRequestException;
import com.project.exception.ResourceNotFoundException;
import com.project.payload.mappers.TourRequestMapper;
import com.project.payload.messages.ErrorMessages;
import com.project.payload.messages.SuccessMessages;
import com.project.payload.request.business.TourRequestRequest;
import com.project.payload.response.ResponseMessage;
import com.project.payload.response.business.tourRequest.TourRequestResponse;
import com.project.repository.business.TourRequestRepository;
import com.project.service.helper.AdvertHelper;
import com.project.service.helper.MethodHelper;
import com.project.service.helper.PageableHelper;
import com.project.service.helper.TourRequestHelper;
import com.project.service.validator.DateTimeValidator;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class TourRequestService {

    private final TourRequestRepository tourRequestRepository;
    private final TourRequestMapper tourRequestMapper;
    private final DateTimeValidator dateTimeValidator;
    private final MethodHelper methodHelper;
    private final PageableHelper pageableHelper;
    private final AdvertHelper advertHelper;
    private final LogService logService;
    private final TourRequestHelper tourRequestHelper;


    /**
     * S01 -> Authenticated (giriş yapmış) kullanıcının tur taleplerini döndürür.
     */
    public Page<TourRequestResponse> getUsersTourRequestWithPageForCustomer(HttpServletRequest httpServletRequest, String query, int page, int size, String sort, String type) {

        String userEmail = (String) httpServletRequest.getAttribute("email");
        User userByEmail = methodHelper.findByUserByEmail(userEmail);

        // Sadece CUSTOMER rolü kontrol edilecek
        methodHelper.checkRoles(userByEmail, RoleType.CUSTOMER);

        // Pagination için Pageable nesnesi oluşturuluyor
        Pageable pageable = pageableHelper.getPageableWithProperties(page, size, sort, type);

        // Eğer arama sorgusu (query) varsa bu sorguya göre filtrelenmiş sonuçlar döndürülüyor
        if (query != null && !query.isEmpty()) {
            return tourRequestRepository.findAllByGuestUserIdAndQuery(userByEmail.getId(), query, pageable)
                    .map(tourRequestMapper::mapTourRequestToTourRequestResponse);
        }
        // Arama sorgusu yoksa sadece kullanıcının tur talepleri döndürülüyor
        else {
            return tourRequestRepository.findAllByGuestUserId(userByEmail.getId(), pageable)
                    .map(tourRequestMapper::mapTourRequestToTourRequestResponse);
        }
    }


    /**
     * S02 -> Tüm tur taleplerini döndürür, yalnızca yönetici ve yönetici yardımcısı rollerine sahip kullanıcılar erişebilir.
     */
    public Page<TourRequestResponse> getAllTourRequestWithPageForAdminAndManager(String query, int page, int size, String sort, String type) {

        // Sayfalama ve sıralama ayarlarını oluşturuyoruz
        Pageable pageable = pageableHelper.getPageableWithProperties(page, size, sort, type);

        // Eğer arama sorgusu yoksa tüm talepleri döndürüyoruz
        if (query == null || query.isEmpty()) {
            return tourRequestRepository.findAll(pageable)
                    .map(tourRequestMapper::mapTourRequestToTourRequestResponse);
        }

        // Eğer arama sorgusu varsa, arama filtresiyle sorgu çalıştırıyoruz
        return tourRequestRepository.getTourRequestsByPageWithQuery(query, pageable)
                .map(tourRequestMapper::mapTourRequestToTourRequestResponse);
    }

    /**
     * S03 -> Authenticated kullanıcının belirli bir tur talebi detayını döndürür.
     */
    public ResponseEntity<TourRequestResponse> getUsersTourRequestDetails(Long id, HttpServletRequest request) {

        // Giriş yapan kullanıcı bilgisi alınır
        User guestUser = methodHelper.getUserByHttpRequest(request);

        // Kullanıcı rolü kontrolü: Sadece CUSTOMER erişebilir
        methodHelper.controlRoles(guestUser, RoleType.CUSTOMER);

        // Kullanıcının talep ettiği ID'ye ait tur talebi aranır
        TourRequest tourRequest = tourRequestRepository.findByIdAndGuestUserId_Id(id, guestUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Tur talebi bulunamadı."));

        // Tur talebi detayları response'a dönüştürülür ve döndürülür
        return ResponseEntity.ok(tourRequestMapper.mapTourRequestToTourRequestResponse(tourRequest));

    }

    /**
     * S04 -> Yönetici veya yönetici yardımcısının bir tur talebinin detaylarını görüntülemesine olanak tanır.
     */
    public TourRequestResponse getUsersTourRequestDetailsForAdmin(Long id) {

        // Tur talebi ID'ye göre aranır
        TourRequest tourRequest = tourRequestHelper.findTourRequestById(id);

        return tourRequestMapper.mapTourRequestToTourRequestResponse(tourRequestHelper.findTourRequestById(id));
    }

    /**
     * S05 -> http://localhost:8080/tour-requests + POST + JSON
     */
    public ResponseMessage<TourRequestResponse> createTourRequest(TourRequestRequest tourRequestRequest, HttpServletRequest request) {

        // Veritabanındaki mevcut tur taleplerini çek
        List<TourRequest> existingTourRequests = tourRequestHelper.getAlTourRequest();

        // Tarih ve saat çakışması var mı kontrol et (İlgili ilanda başka bir tur var mı?)
        dateTimeValidator.checkConflictTourRequestFromRepoByAdvert(existingTourRequests, tourRequestRequest);

        // Giriş yapan kullanıcıyı (guest) al ve onun için çakışan tur talebi var mı kontrol et
        String userEmail = (String) request.getAttribute("email");
        User guestUser  = methodHelper.findByUserByEmail(userEmail);
        dateTimeValidator.checkConflictTourRequestFromRepoByUserForGuest(guestUser, tourRequestRequest);

        // İlan sahibi (owner) için çakışma kontrolü
        Advert advert = advertHelper.getAdvertForFavorites(tourRequestRequest.getAdvertId());
        User ownerUser = advert.getUser();
        dateTimeValidator.checkConflictTourRequestFromRepoByUserForOwner(ownerUser, tourRequestRequest);


        // Tur talebini mapleyerek oluştur ve varsayılan durumunu `PENDING` yap
        TourRequest newTourRequest = tourRequestMapper.mapTourRequestRequestToTourRequest(tourRequestRequest, advert,ownerUser,guestUser);
        newTourRequest.setStatus(TourRequestEnum.PENDING);
        newTourRequest.setOwnerUser(ownerUser);
        newTourRequest.setGuestUser(guestUser);

        // Tur talebini kaydet
        TourRequest savedTourRequest = tourRequestRepository.save(newTourRequest);

        // Log kaydı oluştur
        logService.createLogEvent(guestUser, savedTourRequest.getAdvert(), Log.TOUR_REQUEST_CREATED);

        // Yanıt oluştur ve döndür
        return ResponseMessage.<TourRequestResponse>builder()
                .object(tourRequestMapper.mapTourRequestToTourRequestResponse(savedTourRequest))
                .httpStatus(HttpStatus.CREATED)
                .message(SuccessMessages.TOUR_REQUEST_CREATED)
                .build();
    }

    /**
     * S06 -> Authenticated kullanıcının tur talebini günceller.
     */
    public ResponseMessage<TourRequestResponse> updateTourRequest(TourRequestRequest tourRequestRequest, HttpServletRequest request, Long id) {

            // Kullanıcı rolü kontrolü
            User guestUser = methodHelper.getUserByHttpRequest(request);
            methodHelper.controlRoles(guestUser, RoleType.CUSTOMER);

            // Tur talebi var mı kontrol et
            TourRequest tourRequest = tourRequestHelper.findTourRequestById(id);

            // Sadece beklemede veya reddedilmiş talepler güncellenebilir
            if (tourRequest.getStatus() != TourRequestEnum.PENDING &&
                    tourRequest.getStatus() != TourRequestEnum.CANCELED) {
                throw new BadRequestException(ErrorMessages.TOUR_REQUEST_CAN_NOT_BE_CHANGED);
            }

            // Tarih ve saat çakışması kontrolü (aynı ilan için başka bir talep var mı?)
            List<TourRequest> existingTourRequests = tourRequestHelper.getAlTourRequest();
            dateTimeValidator.checkConflictTourRequestFromRepoByAdvert(existingTourRequests, tourRequestRequest);
            dateTimeValidator.checkConflictTourRequestFromRepoByUserForOwner(tourRequest.getOwnerUser(), tourRequestRequest);
            dateTimeValidator.checkConflictTourRequestFromRepoByUserForGuest(guestUser, tourRequestRequest);

            // Güncelleme için yeni ilan bilgilerini al
            Advert advert = advertHelper.getAdvertForFavorites(tourRequestRequest.getAdvertId());

            // Güncellenmiş tur talebi nesnesini oluştur
            tourRequest.setTourDate(tourRequestRequest.getTourDate());
            tourRequest.setTourTime(tourRequestRequest.getTourTime());
            tourRequest.setAdvert(advert);
            tourRequest.setStatus(TourRequestEnum.PENDING);  // Durumu tekrar "Pending" olarak ayarla
            tourRequest.setGuestUser(guestUser);
            tourRequest.setOwnerUser(advert.getUser());
            tourRequest.setUpdateAt(LocalDateTime.now()); // Güncelleme zamanını ayarla

            // Log kaydı oluştur
            logService.createLogEvent(guestUser, advert, Log.UPDATED);

            // Yanıt oluştur ve döndür
            return ResponseMessage.<TourRequestResponse>builder()
                    .object(tourRequestMapper.mapTourRequestToTourRequestResponse(tourRequestRepository.save(tourRequest)))
                    .httpStatus(HttpStatus.OK)
                    .message(String.format(SuccessMessages.TOUR_REQUEST_UPDATED, id))
                    .build();
    }

    /**
     * S07 -> Authenticated kullanıcının tur talebini iptal eder.
     */
    public ResponseMessage<TourRequestResponse> cancelTourRequest(Long id, HttpServletRequest request) {

        // Kullanıcı rolü kontrolü
        User guestUser = methodHelper.getUserByHttpRequest(request);
        methodHelper.controlRoles(guestUser, RoleType.CUSTOMER);

        // Kullanıcının tur talebi var mı kontrol et
        TourRequest tourRequest = tourRequestRepository.findByIdByCustomer(guestUser.getId(), id);

        // Talep bulunamazsa hata fırlat
        if (tourRequest == null) {
            throw new NotFoundException(ErrorMessages.TOUR_REQUEST_NOT_FOUND);
        }

        // Tur talebinin durumunu iptal olarak ayarla
        tourRequest.setStatus(TourRequestEnum.CANCELED);

        // Log kaydı oluştur
        logService.createLogEvent(guestUser, tourRequest.getAdvert(), Log.TOUR_REQUEST_CANCELED);

        // İptal edilen tur talebini kaydet ve yanıt oluştur
        return ResponseMessage.<TourRequestResponse>builder()
                .object(tourRequestMapper.mapTourRequestToTourRequestResponse(tourRequestRepository.save(tourRequest)))
                .httpStatus(HttpStatus.OK)
                .message(SuccessMessages.TOUR_REQUEST_CANCELLED)
                .build();
    }

    /**
     * S08 -> Authenticated kullanıcının tur talebini onaylar.
     */
    public ResponseMessage<TourRequestResponse> updateTourRequestApprove(Long id, HttpServletRequest httpServletRequest) {
        // Kullanıcı rolü kontrolü
        User guestUser = methodHelper.getUserByHttpRequest(httpServletRequest);
        methodHelper.controlRoles(guestUser, RoleType.CUSTOMER);

        // Kullanıcının tur talebi var mı kontrol et
        TourRequest tourRequest = tourRequestRepository.findByIdByCustomer(guestUser.getId(), id);

        // Talep bulunamazsa hata fırlat
        if (tourRequest == null) {
            throw new NotFoundException(ErrorMessages.TOUR_REQUEST_NOT_FOUND);
        }

        // Talep durumunu onayla
        tourRequest.setStatus(TourRequestEnum.APPROVED);

        // Log kaydı oluştur
        logService.createLogEvent(guestUser, tourRequest.getAdvert(), Log.CREATED);

        // Onaylanan tur talebini kaydet ve yanıt oluştur
        return ResponseMessage.<TourRequestResponse>builder()
                .object(tourRequestMapper.mapTourRequestToTourRequestResponse(tourRequestRepository.save(tourRequest)))
                .httpStatus(HttpStatus.OK)
                .message(SuccessMessages.TOUR_REQUEST_APPROVED)
                .build();
    }

    /**
     * S09 -> Authenticated kullanıcının tur talebini reddeder.
     */
    public ResponseMessage<TourRequestResponse> updateTourRequestDecline(Long id, HttpServletRequest httpServletRequest) {
        // Kullanıcı rolü kontrolü
        User guestUser = methodHelper.getUserByHttpRequest(httpServletRequest);
        methodHelper.controlRoles(guestUser, RoleType.CUSTOMER);

        // Kullanıcının tur talebi var mı kontrol et
        TourRequest tourRequest = tourRequestRepository.findByIdByCustomer(guestUser.getId(), id);

        // Talep bulunamazsa hata fırlat
        if (tourRequest == null) {
            throw new NotFoundException(ErrorMessages.TOUR_REQUEST_NOT_FOUND);
        }

        // Talep durumunu reddet
        tourRequest.setStatus(TourRequestEnum.DECLINED);

        // Log kaydı oluştur
        logService.createLogEvent(guestUser, tourRequest.getAdvert(), Log.TOUR_REQUEST_DECLINED);

        // Reddedilen tur talebini kaydet ve yanıt oluştur
        return ResponseMessage.<TourRequestResponse>builder()
                .object(tourRequestMapper.mapTourRequestToTourRequestResponse(tourRequestRepository.save(tourRequest)))
                .httpStatus(HttpStatus.OK)
                .message(SuccessMessages.TOUR_REQUEST_DECLINED)
                .build();
    }

    /**
     * S10 -> Authenticated kullanıcının tur talebini siler.
     */
    public ResponseMessage<TourRequestResponse> deleteTourRequestById(Long id, HttpServletRequest httpServletRequest) {
            // Kullanıcı rolü kontrolü
            User guestUser = methodHelper.getUserByHttpRequest(httpServletRequest);
            methodHelper.controlRoles(guestUser, RoleType.CUSTOMER);

            // Tur talebini id ile bul
            TourRequest tourRequest = tourRequestHelper.findTourRequestById(id);

            // Tur talebini sil
            tourRequestRepository.delete(tourRequest);

            // Silinen talep nesnesini döndür
            return ResponseMessage.<TourRequestResponse>builder()
                    .object(tourRequestMapper.mapTourRequestToTourRequestResponse(tourRequest))
                    .httpStatus(HttpStatus.OK)
                    .message(SuccessMessages.TOUR_REQUEST_DELETED)
                    .build();
    }

}
