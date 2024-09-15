package com.project.service.helper;

import com.project.entity.concretes.business.TourRequest;
import com.project.exception.ResourceNotFoundException;
import com.project.repository.business.TourRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import com.project.payload.messages.ErrorMessages;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TourRequestHelper {

    private final TourRequestRepository tourRequestRepository;

    public List<TourRequest> getAlTourRequest(){
        return tourRequestRepository.findAll();
    }

    public TourRequest findTourRequestById(Long id){
        return tourRequestRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException(ErrorMessages.TOUR_REQUEST_NOT_FOUND));
    }
}
