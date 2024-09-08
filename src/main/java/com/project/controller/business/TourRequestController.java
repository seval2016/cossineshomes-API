package com.project.controller.business;

import com.project.entity.concretes.business.TourRequest;
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

    // ----> S01
    @GetMapping("/auth")
    @PreAuthorize("hasAnyAuthority('CUSTOMER')")// http://localhost:8080/tour-requests/auth + GET
    public ResponseEntity<Page<TourRequestResponse>> getUsersTourRequestWithPageForCustomer(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @RequestParam(value = "sort", defaultValue = "tourDate") String sort,
            @RequestParam(value = "type", defaultValue = "asc") String type,
            HttpServletRequest servletRequest){

        Page<TourRequestResponse> tourRequestResponses = tourRequestService.getUsersTourRequestWithPageForCustomer(page, size, sort, type, servletRequest);
        return ResponseEntity.ok(tourRequestResponses);
    }

    // ----> S02
    @GetMapping("/admin")
    @PreAuthorize("hasAnyAuthority('MANAGER','ADMIN')") // http://localhost:8080/tour-requests/admin + GET
    public ResponseEntity<Page<TourRequestResponse>> getAllTourRequestWithPageForAdminAndManager(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @RequestParam(value = "sort", defaultValue = "tourDate") String sort,
            @RequestParam(value = "type", defaultValue = "asc") String type,
            HttpServletRequest servletRequest
    ){

        Page<TourRequestResponse> tourRequestResponses  = tourRequestService.getAllTourRequestWithPageForAdminAndManager(page,size,sort,type, servletRequest);

        return ResponseEntity.ok(tourRequestResponses);
    }

    // ----> S03
    @GetMapping("/{id}/auth")
    @PreAuthorize("hasAnyAuthority('CUSTOMER')") //http://localhost:8080/tour-requests/1/auth + GET
    public ResponseEntity<TourRequestResponse> getUsersTourRequestDetails(@PathVariable Long id, HttpServletRequest servletRequest){
        return ResponseEntity.ok(tourRequestService.getUsersTourRequestDetails(id,servletRequest));
    }

    // ----> S04
    @GetMapping("/{id}/admin")
    @PreAuthorize("hasAnyAuthority('MANAGER','ADMIN')") // http://localhost:8080/tour-requests/2/admin + GET
    public ResponseEntity<TourRequestResponse> getUsersTourRequestDetailsForAdminAndManager(@PathVariable Long id, HttpServletRequest servletRequest){

        return ResponseEntity.ok(tourRequestService.getUsersTourRequestDetailsForAdmin(id, servletRequest));
    }

    // ----> S05
    @PostMapping
    @PreAuthorize("hasAnyAuthority('CUSTOMER')") //http://localhost:8080/tour-requests + POST + JSON
    public ResponseMessage<TourRequestResponse> createTourRequest(@RequestBody @Valid TourRequest request, HttpServletRequest servletRequest){
        return  tourRequestService.createTourRequest(request,servletRequest);
    }

    // ----> S06
    @PutMapping("/{id}/auth")
    @PreAuthorize("hasAnyAuthority('CUSTOMER')") // http://localhost:8080/tour-requests/1/auth + PUT + JSON
    public ResponseMessage<TourRequestResponse> updateTourRequest(@PathVariable Long id, @RequestBody @Valid TourRequest request, HttpServletRequest servletRequest){
        return tourRequestService.updateTourRequest(id, request, servletRequest);
    }

    // ----> S07
    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasAnyAuthority('CUSTOMER')") // http://localhost:8080/tour-requests/1/cancel + PATCH + JSON
    public ResponseMessage<TourRequestResponse> cancelTourRequest(@PathVariable Long id,HttpServletRequest servletRequest){
        return tourRequestService.updateTourRequestCancel(id, servletRequest);
    }

    // ----> S08
    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasAnyAuthority('CUSTOMER')") // http://localhost:8080/tour-requests/1/approve + PATCH + JSON
    public ResponseMessage<TourRequestResponse> updateTourRequestApprove(@PathVariable("id") Long id,
                                                                         HttpServletRequest httpServletRequest){
        return tourRequestService.updateTourRequestApprove(id,httpServletRequest);
    }

    // ----> S09
    @PatchMapping("/{id}/decline")
    @PreAuthorize("hasAnyAuthority('CUSTOMER')") // http://localhost:8080/tour-requests/1/decline + PATCH + JSON
    public ResponseMessage<TourRequestResponse> updateTourRequestDecline(
            @PathVariable("id") Long id,
            HttpServletRequest httpServletRequest){

        // status must be pending
        return tourRequestService.updateTourRequestDecline(id,httpServletRequest);
    }

    // ----> S10
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')") // http://localhost:8080/tour-requests/1 + DELETE
    public ResponseMessage<TourRequestResponse> deleteTourRequest(@PathVariable("id") Long id,
                                                                  HttpServletRequest httpServletRequest){
        return tourRequestService.deleteTourRequest(id,httpServletRequest);
    }

}