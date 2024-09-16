package com.project.controller.business;

import com.project.entity.concretes.business.TourRequest;
import com.project.payload.request.business.TourRequestRequest;
import com.project.payload.response.business.ResponseMessage;
import com.project.payload.response.business.TourRequestResponse;
import com.project.service.business.TourRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/tour-requests")
@RequiredArgsConstructor
public class TourRequestController {

    private final TourRequestService tourRequestService;

    // ----> S01 - It should return the authenticated user’s tour requests
    @GetMapping("/auth")//http://localhost:8080/tour-requests/auth?q=blabla&page=1&size=10&sort=date&type=asc+ GET
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

    // ----> S02 - It should return tour requests
    @GetMapping("/admin") //http://localhost:8080/tour-requests/auth?q=blabla&page=1&size=10&sort=date&type=asc + GET
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

    // ----> S03 - It should return the authenticated user’s tour request detail
    @GetMapping("/{id}/auth") // http://localhost:8080/tour-requests/3/auth + GET
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','CUSTOMER')")
    public ResponseEntity<TourRequestResponse> getTourRequestByIdAuth(@PathVariable Long id,
                                                                      HttpServletRequest httpServletRequest) {
        return tourRequestService.getUsersTourRequestDetails(id, httpServletRequest);
    }

    // ----> S04 - It should return a tour request detail
    @GetMapping("/{id}/admin")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')") // http://localhost:8080/tour-requests/23/admin + GET
    public ResponseEntity<TourRequestResponse> getUsersTourRequestByIdForAdminAndManager(@PathVariable Long id, HttpServletRequest servletRequest) {
        return tourRequestService.getUsersTourRequestDetailsForAdmin(id, servletRequest);
    }

    // ----> S05 - It will create a tour request
    @PostMapping() //http://localhost:8080/tour-requests + POST + JSON
    @PreAuthorize("hasAnyAuthority('CUSTOMER')")
    public ResponseMessage<TourRequestResponse> createTourRequest(@RequestBody @Valid TourRequestRequest tourRequestRequest, HttpServletRequest request) {
        return tourRequestService.createTourRequest(tourRequestRequest, request);
    }

    // ----> S06 - It will update a tour request
    @PutMapping("/{id}/auth")//http://localhost:8080/tour-requests/45 + PUT + JSON
    @PreAuthorize("hasAnyAuthority('CUSTOMER')")
    public ResponseMessage<TourRequestResponse> updateTourRequest(@RequestBody @Valid TourRequestRequest tourRequestRequest, HttpServletRequest request, @PathVariable Long id) {
        return tourRequestService.updateTourRequest(tourRequestRequest, request, id);
    }

    // ----> S07 - It will cancel the authenticated user’s tour request
    @PatchMapping("/{id}/cancel")//http://localhost:8080/tour-requests/45/cancel + PATCH + JSON
    @PreAuthorize("hasAnyAuthority('CUSTOMER')")
    public ResponseMessage<TourRequestResponse> cancelTourRequest(@PathVariable Long id, HttpServletRequest request) {
        return tourRequestService.updateTourRequestCancel(id, request);
    }

    // ----> S08 - It should approve the tour request
    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasAnyAuthority('CUSTOMER')") //http://localhost:8080/tour-requests/4/approve + PATCH + JSON
    public ResponseMessage<TourRequestResponse> updateTourRequestApprove(@PathVariable("id") Long id, HttpServletRequest httpServletRequest) {
        return tourRequestService.updateTourRequestApprove(id, httpServletRequest);
    }

    // ----> S09 - It should decline the tour request
    @PatchMapping("/{id}/decline")
    @PreAuthorize("hasAnyAuthority('CUSTOMER')") // http://localhost:8080/tour-requests/4/decline + PATCH + JSON
    public ResponseMessage<TourRequestResponse> updateTourRequestDecline(
            @PathVariable("id") Long id,
            HttpServletRequest httpServletRequest) {
        return tourRequestService.updateTourRequestDecline(id, httpServletRequest);
    }

    // ----> S10 - It will delete a tour request
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')") // http://localhost:8080/tour-requests/5 + DELETE
    public ResponseMessage<TourRequestResponse> deleteTourRequestById(@PathVariable("id") Long id,
                                                                      HttpServletRequest httpServletRequest) {
        return tourRequestService.deleteTourRequestById(id, httpServletRequest);
    }

}