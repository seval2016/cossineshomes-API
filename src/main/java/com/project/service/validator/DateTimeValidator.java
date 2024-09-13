package com.project.service.validator;

import com.project.entity.concretes.business.TourRequest;
import com.project.entity.concretes.user.User;
import com.project.exception.BadRequestException;
import com.project.exception.ConflictException;
import com.project.payload.messages.ErrorMessages;
import com.project.payload.request.business.TourRequestRequest;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Component
public class DateTimeValidator {


    public void checkConflictTourRequestFromRepoByAdvert(List<TourRequest> tourRequestsFromRepo, TourRequestRequest tourRequestRequest) {

        for(TourRequest tourRequest: tourRequestsFromRepo ){
            if((tourRequest.getAdvert().getId().equals(tourRequestRequest.getAdvertId())) &&
                    (tourRequest.getTourDate().equals(tourRequestRequest.getTourDate()))) {

                long betweenMinutesTime = calculateMinutesBetweenTime(tourRequest,tourRequestRequest);

                if(Math.abs(betweenMinutesTime)<120){
                    // Tour sürelerini 30 dk olarak belirledim
                    throw new ConflictException(ErrorMessages.CONFLICT_TOUR_REQUEST_TIME);
                }
            }
        }
    }

    public void checkConflictTourRequestFromRepoByUserForGuest(User userGuest, TourRequestRequest tourRequestRequest) {

        for(TourRequest tourRequest : userGuest.getGuestTourRequests()){
            if(tourRequest.getTourDate().equals(tourRequestRequest.getTourDate())){

                if(Math.abs(calculateMinutesBetweenTime(tourRequest,tourRequestRequest))<30){
                    // Tour sürelerini 30 dk olarak belirledim
                    throw new ConflictException(ErrorMessages.CONFLICT_TOUR_REQUEST_TIME);

                }
            }
        }
    }
    public void checkConflictTourRequestFromRepoByUserForOwner(User ownerUser, TourRequestRequest tourRequestRequest) {

        for (TourRequest tourRequest : ownerUser.getOwnerTourRequests()){
            if(tourRequest.getTourDate().equals(tourRequestRequest.getTourDate())){

                if(calculateMinutesBetweenTime(tourRequest,tourRequestRequest) <30){
                    throw new ConflictException(ErrorMessages.CONFLICT_TOUR_REQUEST_TIME);

                }
            }
        }
    }

    private long calculateMinutesBetweenTime(TourRequest tourRequest,TourRequestRequest tourRequestRequest){

        LocalTime tourTimeFromRepo = tourRequest.getTourTime();

        LocalTime requestTime = tourRequestRequest.getTourTime();

        return Duration.between(tourTimeFromRepo,requestTime).toMinutes();

    }

    public void checkBeginTimeAndEndTime(LocalDateTime begin, LocalDateTime end) {

        if(begin.isAfter(end)){
            throw new BadRequestException(ErrorMessages.BEGIN_TIME_CAN_NOT_BE_AFTER_END_TIME);
        }

    }


}


