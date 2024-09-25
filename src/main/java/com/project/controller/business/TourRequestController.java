package com.project.controller.business;

import com.project.payload.request.business.TourRequestRequest;
import com.project.payload.response.ResponseMessage;
import com.project.payload.response.business.tourRequest.TourRequestResponse;
import com.project.service.business.TourRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/tour-requests")
@RequiredArgsConstructor
public class TourRequestController {

    private final TourRequestService tourRequestService;

    /**
     * S01 -> http://localhost:8080/tour-requests/auth?q=blabla&page=1&size=10&sort=date&type=asc
     *
     * Authenticated (giriş yapmış) kullanıcının tur taleplerini döndürür.
     *
     * Response: Page<TourRequestResponse>
     */
    @GetMapping("/auth")
    @PreAuthorize("hasAnyAuthority('CUSTOMER')")
    public ResponseEntity<Page<TourRequestResponse>> getUsersTourRequestWithPageForCustomer(
            HttpServletRequest httpServletRequest,
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @RequestParam(value = "sort", defaultValue = "tourDate") String sort,
            @RequestParam(value = "type", defaultValue = "asc") String type) {
        return ResponseEntity.ok(tourRequestService.getUsersTourRequestWithPageForCustomer(httpServletRequest, query, page, size, sort, type));
    }

    /**
     * S02 -> http://localhost:8080/tour-requests/auth?q=blabla&page=1&size=10&sort=date&type=asc + GET
     *
     * Tüm tur taleplerini döndürür, yalnızca yönetici ve yönetici yardımcısı rollerine sahip kullanıcılar erişebilir.
     *
     */
    @GetMapping("/admin")
    @PreAuthorize("hasAnyAuthority('MANAGER','ADMIN')")
    public ResponseEntity<Page<TourRequestResponse>> getAllTourRequestWithPageForAdminAndManager(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @RequestParam(value = "sort", defaultValue = "tourDate") String sort,
            @RequestParam(value = "type", defaultValue = "asc") String type
    ) {
        return ResponseEntity.ok(tourRequestService.getAllTourRequestWithPageForAdminAndManager(query, page, size, sort, type));
    }

    /**
     * S03 -> http://localhost:8080/tour-requests/3/auth + GET
     *
     * Authenticated kullanıcının belirli bir tur talebi detayını döndürür.
     *
     * Payload:
     * - id: Tur talebi ID'si
     *
     * Response:
     * - Tek bir tur talebi objesi (tour-request)
     */
    @GetMapping("/{id}/auth")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','CUSTOMER')")
    public ResponseEntity<TourRequestResponse> getTourRequestByIdAuth(@PathVariable Long id,
                                                                      HttpServletRequest httpServletRequest) {
        return tourRequestService.getUsersTourRequestDetails(id, httpServletRequest);
    }

    /**
     * S04 -> http://localhost:8080/tour-requests/:id/admin
     *
     * Yönetici veya yönetici yardımcısının bir tur talebinin detaylarını görüntülemesine olanak tanır.
     *
     * Payload:
     * - id: Tur talebi ID'si
     *
     * Response:
     * - Tek bir tur talebi objesi (tour-request)
     */
    @GetMapping("/{id}/admin")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseEntity<TourRequestResponse> getUsersTourRequestByIdForAdminAndManager(@PathVariable Long id) {
        return ResponseEntity.ok(tourRequestService.getUsersTourRequestDetailsForAdmin(id));
    }

    /**
     * S05 -> http://localhost:8080/tour-requests + POST + JSON
     *
     * Yeni bir tur talebi oluşturur, yalnızca authenticated kullanıcılar bu işlemi yapabilir.
     *
     * Payload (Body):
     * - tour_date: Tur tarihi
     * - tour_time: Tur saati
     * - advert_id: İlan ID'si
     *
     * Response:
     * - Oluşturulan tur talebi objesi (tour_request)
     *
     * Gereksinimler:
     * - Oluşturulan tur talebinin varsayılan durumu 0 (beklemede) olmalıdır.
     */
    @PostMapping()
    @PreAuthorize("hasAnyAuthority('CUSTOMER')")
    public ResponseMessage<TourRequestResponse> createTourRequest(@RequestBody @Valid TourRequestRequest tourRequestRequest, HttpServletRequest request) {
        return tourRequestService.createTourRequest(tourRequestRequest, request);
    }

    /**
     * S06 -> http://localhost:8080/tour-requests/45 + PUT + JSON
     *
     * Authenticated kullanıcının tur talebini günceller.
     *
     * Payload (Body):
     * - tour_date: Tur tarihi
     * - tour_time: Tur saati
     * - advert_id: İlan ID'si
     *
     * Response:
     * - Güncellenmiş tur talebi objesi (tour_request)
     *
     * Gereksinimler:
     * - Sadece durumu beklemede veya reddedilmiş olan talepler güncellenebilir.
     * - Güncelleme sonrası durum yeniden "beklemede" olarak ayarlanmalıdır.
     */
    @PutMapping("/{id}/auth")
    @PreAuthorize("hasAnyAuthority('CUSTOMER')")
    public ResponseMessage<TourRequestResponse> updateTourRequest(@RequestBody @Valid TourRequestRequest tourRequestRequest, HttpServletRequest request, @PathVariable Long id) {
        return tourRequestService.updateTourRequest(tourRequestRequest, request, id);
    }

    /**
     * S07 -> http://localhost:8080/tour-requests/45/cancel + PATCH + JSON
     *
     * Authenticated kullanıcının tur talebini iptal eder.
     *
     * Payload (Path):
     * - id: Tur talebi ID'si
     *
     * Response:
     * - İptal edilen tur talebi objesi (tour_request)
     */
    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasAnyAuthority('CUSTOMER')")
    public ResponseMessage<TourRequestResponse> cancelTourRequest(@PathVariable Long id, HttpServletRequest request) {
        return tourRequestService.cancelTourRequest(id, request);
    }

    /**
     * S08 -> http://localhost:8080/tour-requests/4/approve + PATCH + JSON
     *
     * Authenticated kullanıcının tur talebini onaylar.
     *
     * Payload:
     * - id: Tur talebi ID'si
     *
     * Response:
     * - Onaylanmış tur talebi objesi (tour_request)
     */
    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasAnyAuthority('CUSTOMER')")
    public ResponseMessage<TourRequestResponse> updateTourRequestApprove(@PathVariable("id") Long id, HttpServletRequest httpServletRequest) {
        return tourRequestService.updateTourRequestApprove(id, httpServletRequest);
    }

    /**
     * S09 -> http://localhost:8080/tour-requests/4/decline + PATCH + JSON
     *
     * Authenticated kullanıcının tur talebini reddeder.
     *
     * Payload:
     * - id: Tur talebi ID'si
     *
     * Response:
     * - Reddedilmiş tur talebi objesi (tour_request)
     */
    @PatchMapping("/{id}/decline")
    @PreAuthorize("hasAnyAuthority('CUSTOMER')")
    public ResponseMessage<TourRequestResponse> updateTourRequestDecline(
            @PathVariable("id") Long id,
            HttpServletRequest httpServletRequest) {
        return tourRequestService.updateTourRequestDecline(id, httpServletRequest);
    }

    /**
     * S10 -> http://localhost:8080/tour-requests/5 + DELETE
     *
     * Authenticated kullanıcının tur talebini siler.
     *
     * Payload:
     * - id: Tur talebi ID'si
     *
     * Response:
     * - Silinen tur talebi objesi (tour_request)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseMessage<TourRequestResponse> deleteTourRequestById(@PathVariable("id") Long id,
                                                                      HttpServletRequest httpServletRequest) {
        return tourRequestService.deleteTourRequestById(id, httpServletRequest);
    }

}