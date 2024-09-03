package com.project.service.business;

import com.project.entity.concretes.business.Advert;
import com.project.exception.ResourceNotFoundException;
import com.project.payload.mappers.AdvertMapper;
import com.project.payload.messages.ErrorMessages;
import com.project.payload.messages.SuccessMessages;
import com.project.payload.request.business.AdvertRequest;
import com.project.payload.response.business.AdvertResponse;
import com.project.payload.response.business.ResponseMessage;
import com.project.repository.business.AdvertRepository;
import com.project.service.helper.PageableHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdvertService {

    private final AdvertRepository advertRepository;
    private final AdvertMapper advertMapper;
    private final PageableHelper pageableHelper;

    public ResponseMessage<AdvertResponse> saveAdvert(AdvertRequest advertRequest) {

        Advert savedAdvert = advertRepository.save(advertMapper.mapAdvertRequestToAdvert(advertRequest));

        return ResponseMessage.<AdvertResponse>builder()
                .message((SuccessMessages.ADVERT_SAVED))
                .object(advertMapper.mapAdvertToAdvertResponse(savedAdvert))
                .httpStatus(HttpStatus.CREATED)
                .build();
    }

    public AdvertResponse getAdvertById(Long id) {
        Advert advert = isAdvertExist(id);
        return advertMapper.mapAdvertToAdvertResponse(advert);
    }

    private Advert isAdvertExist(Long id){
        return advertRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException(String.format(ErrorMessages.ADVERT_NOT_FOUND, id)));
    }

    public List<AdvertResponse> getAllAdverts() {

        return advertRepository.findAll()
                .stream()
                .map(advertMapper::mapAdvertToAdvertResponse)
                .collect(Collectors.toList());
    }

    public Page<AdvertResponse> getAllAdvertsByPage(int page, int size, String sort, String type) {
        Pageable pageable = pageableHelper.getPageableWithProperties(page, size, sort, type);
        return advertRepository.findAll(pageable)
                .map(advertMapper::mapAdvertToAdvertResponse);
    }


    public ResponseMessage deleteAdvertById(Long id) {
        isAdvertExist(id);
        advertRepository.deleteById(id);

        return ResponseMessage.builder()
                .message(SuccessMessages.ADVERT_DELETED)
                .httpStatus(HttpStatus.OK)
                .build();

    }

    public ResponseMessage<AdvertResponse> updateAdvertById(Long id, AdvertRequest advertRequest) {
        isAdvertExist(id);
        Advert advertUpdated =  advertRepository.save(advertMapper.mapAdvertRequestToUpdatedAdvert(id, advertRequest));
        return ResponseMessage.<AdvertResponse>builder()
                .message(SuccessMessages.ADVERT_UPDATED)
                .httpStatus(HttpStatus.OK)
                .object(advertMapper.mapAdvertToAdvertResponse(advertUpdated))
                .build();
    }
}