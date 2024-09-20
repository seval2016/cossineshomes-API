package com.project.service.business;

import com.project.entity.concretes.business.AdvertType;
import com.project.exception.BadRequestException;
import com.project.exception.ConflictException;
import com.project.exception.ResourceNotFoundException;
import com.project.payload.mappers.AdvertTypesMapper;
import com.project.payload.messages.ErrorMessages;
import com.project.payload.messages.SuccessMessages;
import com.project.payload.request.business.AdvertTypeRequest;
import com.project.payload.response.business.advert.AdvertTypeResponse;
import com.project.payload.response.business.ResponseMessage;
import com.project.repository.business.AdvertTypesRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdvertTypesService {

    private final AdvertTypesRepository advertTypesRepository;
    private final AdvertTypesMapper advertTypeMapper;

    public List<AdvertTypeResponse> getAllAdvertTypes() {
        return advertTypesRepository.findAll()
                .stream()
                .map(advertTypeMapper::mapAdvertTypeToAdvertTypeResponse)
                .collect(Collectors.toList());
    }

    public AdvertTypeResponse getAdvertTypeById(Long id) {
        AdvertType advertType= isAdvertTypeExists(id);
        return advertTypeMapper.mapAdvertTypeToAdvertTypeResponse(advertType);
    }

    //yardımcı metod: id ile advert type arama
    private AdvertType isAdvertTypeExists(Long id){
        return advertTypesRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException(String.format(ErrorMessages.ADVERT_TYPE_NOT_FOUND_BY_ID , id)));
    }

    public ResponseMessage<AdvertTypeResponse> saveAdvertType(AdvertTypeRequest advertTypeRequest) {
        isAdvertTypeExistsByTitle(advertTypeRequest.getTitle());

        AdvertType savedAdvertType= advertTypesRepository.save(advertTypeMapper.mapAdvertTypeRequestToAdvertType(advertTypeRequest));

        return ResponseMessage.<AdvertTypeResponse>builder()
                .message(SuccessMessages.ADVERT_TYPE_SAVED)
                .httpStatus(HttpStatus.CREATED)
                .object(advertTypeMapper.mapAdvertTypeToAdvertTypeResponse(savedAdvertType))
                .build();
    }

    //yardımcı metod: title ile advert type arama
    public boolean isAdvertTypeExistsByTitle(String title){
        boolean advertTypeTitleExist= advertTypesRepository.existsByTitle(title);

        if (advertTypeTitleExist){
            throw new ConflictException(ErrorMessages.ADVERT_TYPE_ALREADY_EXIST);
        }else {
            return false;
        }
    }


    public AdvertTypeResponse updateAdvertTypeById(Long id, AdvertTypeRequest advertTypeRequest) {
        AdvertType advertType= isAdvertTypeExists(id);

        if(
                !(advertType.getTitle().equalsIgnoreCase(advertTypeRequest.getTitle())) &&
                        (advertTypesRepository.existsByTitle(advertTypeRequest.getTitle()))){
            throw new ConflictException(ErrorMessages.ADVERT_TYPE_ALREADY_EXIST);
        }

        AdvertType updatedAdvertType= advertTypeMapper.mapAdvertRequestToUpdatedAdvertType(id, advertTypeRequest);

        AdvertType savedAdvertType= advertTypesRepository.save(updatedAdvertType);

        return advertTypeMapper.mapAdvertTypeToAdvertTypeResponse(savedAdvertType);
    }

    public ResponseMessage deleteAdvertTypeById(Long id) {
        isAdvertTypeExists(id);
        advertTypesRepository.deleteById(id);

        return ResponseMessage.builder()
                .message(SuccessMessages.ADVERT_TYPE_DELETED)
                .httpStatus(HttpStatus.OK)
                .build();

    }

    //Advert için yazıldı
    public AdvertType getAdvertTypeByIdForAdvert(Long advertTypeId) {
        return isAdvertTypeExists(advertTypeId);
    }

    @Transactional
    public void resetAdvertTypesTables() {
        //     advertTypesRepository.deleteByBuiltIn(false);
    }

    public AdvertType findByTitle(String type) {

        return   advertTypesRepository.findByTitle(type).orElseThrow(()-> new BadRequestException("AdvertType is not found."));

    }

    public void saveAdvertTypeRunner(AdvertType advertType) {
        advertTypesRepository.save(advertType);
    }

    public AdvertType findByIdAdvertType(long l) {
        return advertTypesRepository.findById(l).orElseThrow(()->new ResourceNotFoundException("AdvertType is not found"));
    }
}
