package com.project.service.business;

import com.project.entity.concretes.business.TourRequest;
import com.project.payload.response.business.ResponseMessage;
import com.project.payload.response.business.TourRequestResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;


@Service
@RequiredArgsConstructor
public class TourRequestService {

    public Page<TourRequestResponse> getUsersTourRequestWithPageForCustomer(int page, int size, String sort, String type, HttpServletRequest servletRequest) {
    return null;
    }

    public Page<TourRequestResponse> getAllTourRequestWithPageForAdminAndManager(int page, int size, String sort, String type, HttpServletRequest servletRequest) {
        return null;
    }

    public TourRequestResponse getUsersTourRequestDetails(Long id, HttpServletRequest servletRequest) {
        return null;
    }

    public TourRequestResponse getUsersTourRequestDetailsForAdmin(Long id, HttpServletRequest servletRequest) {
        return null;
    }

    public ResponseMessage<TourRequestResponse> createTourRequest(TourRequest request, HttpServletRequest servletRequest) {
        return null;
    }

    public ResponseMessage<TourRequestResponse> updateTourRequest(Long id, TourRequest request, HttpServletRequest servletRequest) {
        return null;
    }

    public ResponseMessage<TourRequestResponse> updateTourRequestCancel(Long id, HttpServletRequest servletRequest) {
        return null;
    }

    public ResponseMessage<TourRequestResponse> updateTourRequestApprove(Long id, HttpServletRequest httpServletRequest) {
        return null;
    }

    public ResponseMessage<TourRequestResponse> updateTourRequestDecline(Long id, HttpServletRequest httpServletRequest) {
        return null;
    }

    public ResponseMessage<TourRequestResponse> deleteTourRequest(Long id, HttpServletRequest httpServletRequest) {
        return null;
    }
}
