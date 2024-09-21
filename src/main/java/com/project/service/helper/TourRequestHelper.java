package com.project.service.helper;

import com.project.entity.concretes.business.Advert;
import com.project.entity.concretes.business.TourRequest;
import com.project.entity.concretes.user.User;
import com.project.entity.enums.RoleType;
import com.project.entity.enums.StatusType;
import com.project.exception.ResourceNotFoundException;
import com.project.payload.mappers.TourRequestMapper;
import com.project.payload.response.ResponseMessage;
import com.project.payload.response.business.tourRequest.TourRequestResponse;
import com.project.repository.business.TourRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import com.project.payload.messages.ErrorMessages;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TourRequestHelper {

    private final TourRequestRepository tourRequestRepository;
    private final MethodHelper methodHelper;
    private final AdvertHelper advertHelper;
    private final TourRequestMapper tourRequestMapper;

    public List<TourRequest> getAlTourRequest() {
        return tourRequestRepository.findAll();
    }

    public TourRequest findTourRequestById(Long id) {
        return tourRequestRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(ErrorMessages.TOUR_REQUEST_NOT_FOUND));
    }


    public List<TourRequest> getTourRequest(LocalDateTime date1, LocalDateTime date2, StatusType statusType) {

        return tourRequestRepository.getTourRequest(date1, date2, statusType);

    }

    public void resetTourRequestTables() {
        tourRequestRepository.deleteAll();
    }

    public Set<TourRequest> getTourRequestsById(Set<Long> tourRequestIdList) {

        return tourRequestRepository.findByIdIn(tourRequestIdList);

    }

    @Transactional
    public ResponseMessage<List<TourRequestResponse>> getTourRequestByAdvertId(Long id, HttpServletRequest request) {
        User user = methodHelper.getUserByHttpRequest(request);
        methodHelper.controlRoles(user, RoleType.ADMIN, RoleType.CUSTOMER, RoleType.MANAGER);
        Advert advert = advertHelper.isAdvertExistById(id);
        List<TourRequest> tourRequestList = getTourRequestByAdvert(advert);
        return ResponseMessage.<List<TourRequestResponse>>builder()
                .message("Advert`s TourRequest fetched successfully")
                .status(HttpStatus.OK)
                .object(tourRequestList.stream().map(tourRequestMapper::mapTourRequestToTourRequestResponse).collect(Collectors.toList()))
                .build();
    }

    private List<TourRequest> getTourRequestByAdvert(Advert advert) {
        return tourRequestRepository.findByAdvert(advert).orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMessages.THERE_IS_NO_TOUR_REQUEST_OF_ADVERT, advert.getId())));
    }


}
