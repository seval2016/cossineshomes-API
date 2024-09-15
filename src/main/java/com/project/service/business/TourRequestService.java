package com.project.service.business;

import com.project.entity.concretes.business.Advert;
import com.project.entity.concretes.business.TourRequest;
import com.project.entity.concretes.user.User;
import com.project.entity.enums.LogEnum;
import com.project.entity.enums.RoleType;
import com.project.entity.enums.StatusType;
import com.project.exception.BadRequestException;
import com.project.exception.ResourceNotFoundException;
import com.project.payload.mappers.TourRequestMapper;
import com.project.payload.messages.ErrorMessages;
import com.project.payload.messages.SuccessMessages;
import com.project.payload.request.business.TourRequestRequest;
import com.project.payload.response.business.ResponseMessage;
import com.project.payload.response.business.TourRequestResponse;
import com.project.repository.business.TourRequestRepository;
import com.project.service.helper.AdvertHelper;
import com.project.service.helper.MethodHelper;
import com.project.service.helper.PageableHelper;
import com.project.service.helper.TourRequestHelper;
import com.project.service.user.UserRoleService;
import com.project.service.validator.DateTimeValidator;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TourRequestService {

    private final TourRequestRepository tourRequestRepository;
    private final TourRequestMapper tourRequestMapper;
    private final DateTimeValidator dateTimeValidator;
    private final MethodHelper methodHelper;
    private final UserRoleService userRoleService;
    private final PageableHelper pageableHelper;
    private  final AdvertHelper advertHelper;
    private final LogService logService;
    private final TourRequestHelper tourRequestHelper;

    // ----> S01
    public Page<TourRequestResponse> getUsersTourRequestWithPageForCustomer(HttpServletRequest httpServletRequest,String query, int page, int size, String sort, String type) {
        String userEmail = (String) httpServletRequest.getAttribute("email");
        User userByEmail = methodHelper.findByUserByEmail(userEmail);
        methodHelper.checkRoles(userByEmail, RoleType.CUSTOMER, RoleType.ADMIN);

        Pageable pageable = pageableHelper.getPageableWithProperties(page, size, sort, type);

        if (query != null && !query.isEmpty()) {
            return tourRequestRepository.findAllByGuestUserId_IdAndQuery(userByEmail.getId(), query, pageable)
                    .map(tourRequestMapper::mapTourRequestToTourRequestResponse);
        } else {

            return tourRequestRepository.findAllByGuestUserId_Id(userByEmail.getId(), pageable)
                    .map(tourRequestMapper::mapTourRequestToTourRequestResponse);
        }
    }

        // ----> S02
        public Page<TourRequestResponse> getAllTourRequestWithPageForAdminAndManager(String query, int page, int size, String sort, String type) {
        Pageable pageable= pageableHelper.getPageableWithProperties(page,size,sort,type);

        if (query == null || query.isEmpty()) {
            return tourRequestRepository.findAll(pageable).map(tourRequestMapper::mapTourRequestToTourRequestResponse);
        }
        return tourRequestRepository.getTourRequestsByPageWithQuery(query, pageable)
                .map(tourRequestMapper::mapTourRequestToTourRequestResponse);
    }

        // ----> S03
        public ResponseEntity<TourRequestResponse> getUsersTourRequestDetails (Long id, HttpServletRequest request){
            //Rol kontrolü
            User guestUser =methodHelper.getUserByHttpRequest(request);
            methodHelper.controlRoles(guestUser,RoleType.CUSTOMER);

            TourRequest tourRequest = tourRequestRepository.findByIdAndGuestUserId_Id(id, guestUser.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Tour request is not found for this user"));

            return ResponseEntity.ok(tourRequestMapper.mapTourRequestToTourRequestResponse(tourRequest));
        }

        // ----> S04
        public ResponseEntity<TourRequestResponse> getUsersTourRequestDetailsForAdmin (Long id, HttpServletRequest request){
            //Rol kontrolü
            User guestUser =methodHelper.getUserByHttpRequest(request);
            methodHelper.controlRoles(guestUser,RoleType.ADMIN,RoleType.MANAGER);

            return ResponseEntity.ok(tourRequestMapper.mapTourRequestToTourRequestResponse(tourRequestHelper.findTourRequestById(id)));
        }

        // ----> S05
        public ResponseMessage<TourRequestResponse> createTourRequest (TourRequestRequest tourRequestRequest, HttpServletRequest request){

        // Advertta Date-time cakismasi var mi?
        List<TourRequest> tourRequestsFromRepo = tourRequestHelper.getAlTourRequest();
        dateTimeValidator.checkConflictTourRequestFromRepoByAdvert(tourRequestsFromRepo,tourRequestRequest);

        //UserGuest için aynı satte requesti var mı?
        String userEmail = (String) request.getAttribute("email");
        User userGuest = methodHelper.findByUserByEmail(userEmail);
        dateTimeValidator.checkConflictTourRequestFromRepoByUserForGuest(userGuest,tourRequestRequest);

        //UserOwner için çakışma kontrolü
        Advert advert = advertHelper.getAdvertForFavorites(tourRequestRequest.getAdvertId());
        User ownerUser = advert.getUser();
        // User ownerUser = methodHelper.findUserWithId(ownerId);
        dateTimeValidator.checkConflictTourRequestFromRepoByUserForOwner(ownerUser,tourRequestRequest);

        if (userGuest.getId().equals(ownerUser.getId())) {
            throw new BadRequestException("Can not book your own advert");
        }
        TourRequest mappedTourRequest = tourRequestMapper.mapTourRequestRequestToTourRequest(tourRequestRequest,advert);
        mappedTourRequest.setStatus(StatusType.PENDING);
        mappedTourRequest.setOwnerUser(ownerUser);
        mappedTourRequest.setGuestUser(userGuest);

        TourRequest savedTourRequest = tourRequestRepository.save(mappedTourRequest);

        logService.createLogEvent(userGuest,savedTourRequest.getAdvert(), LogEnum.TOUR_REQUEST_CREATED);


        return ResponseMessage.<TourRequestResponse>builder()
                .object(tourRequestMapper.mapTourRequestToTourRequestResponse(savedTourRequest))
                .httpStatus(HttpStatus.CREATED)
                .message(SuccessMessages.TOUR_REQUEST_CREATED)
                .build();
        }

        // ----> S06
        public ResponseMessage<TourRequestResponse> updateTourRequest ( TourRequestRequest tourRequestRequest,HttpServletRequest request, Long id){
            //Rol kontrolü
            User guestUser =methodHelper.getUserByHttpRequest(request);
            methodHelper.controlRoles(guestUser,RoleType.CUSTOMER);

            //id ile tour request var mi?
            TourRequest tourRequest = tourRequestHelper.findTourRequestById(id);

            if(tourRequest.getStatus().getTourStatusValue()==1){
                throw new BadRequestException(ErrorMessages.TOUR_REQUEST_CAN_NOT_BE_CHANGED);
            }

            List<TourRequest> tourRequestList = tourRequestHelper.getAlTourRequest();

            dateTimeValidator.checkConflictTourRequestFromRepoByAdvert(tourRequestList,tourRequestRequest);
            dateTimeValidator.checkConflictTourRequestFromRepoByUserForOwner(tourRequest.getOwnerUser(),tourRequestRequest);
            dateTimeValidator.checkConflictTourRequestFromRepoByUserForGuest(guestUser,tourRequestRequest);

            //request to entity
            Advert advert = advertHelper.getAdvertForFavorites(tourRequestRequest.getAdvertId());
            TourRequest updatedTourRequest = tourRequestMapper.mapTourRequestRequestToTourRequest(tourRequestRequest,advert);
            updatedTourRequest.setId(id);
            updatedTourRequest.setStatus(StatusType.PENDING);
            updatedTourRequest.setGuestUser(guestUser);
            updatedTourRequest.setOwnerUser(advert.getUser());
            updatedTourRequest.setCreateAt(LocalDateTime.now());


            logService.createLogEvent(guestUser,advert, LogEnum.TOUR_REQUEST_ACCEPTED);

            return ResponseMessage.<TourRequestResponse>builder()
                    .object(tourRequestMapper.mapTourRequestToTourRequestResponse( tourRequestRepository.save(updatedTourRequest)))
                    .httpStatus(HttpStatus.OK)
                    .message(String.format(SuccessMessages.TOUR_REQUEST_UPDATED,id))
                    .build();
        }

        public ResponseMessage<TourRequestResponse> updateTourRequestCancel (Long id, HttpServletRequest servletRequest)
        {
            return null;
        }

        public ResponseMessage<TourRequestResponse> updateTourRequestApprove (Long id, HttpServletRequest
        httpServletRequest){
            return null;
        }

        public ResponseMessage<TourRequestResponse> updateTourRequestDecline (Long id, HttpServletRequest
        httpServletRequest){
            return null;
        }

        public ResponseMessage<TourRequestResponse> deleteTourRequest (Long id, HttpServletRequest httpServletRequest){
            return null;
        }
        /*******************/

        public List<TourRequest> getTourRequest (LocalDateTime date1, LocalDateTime date2, StatusType statusType){

            return tourRequestRepository.getTourRequest(date1, date2, statusType);

        }
    }

    }
