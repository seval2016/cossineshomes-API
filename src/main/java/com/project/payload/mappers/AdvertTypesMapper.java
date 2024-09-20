package com.project.payload.mappers;

import com.project.entity.concretes.business.AdvertType;
import com.project.payload.request.business.AdvertTypeRequest;
import com.project.payload.response.business.advert.AdvertTypeResponse;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class AdvertTypesMapper {
    //DTO-> POJO

    public AdvertType mapAdvertTypeRequestToAdvertType(AdvertTypeRequest advertTypeRequest){
        return AdvertType.builder()
                .title(advertTypeRequest.getTitle().toLowerCase())
                .build();
    }

    //POJO -> DTO

    public AdvertTypeResponse mapAdvertTypeToAdvertTypeResponse(AdvertType advertType){
        return AdvertTypeResponse.builder()
                .id(advertType.getId())
                .title(advertType.getTitle())
                .build();
    }

    //request -> updated pojo

    public AdvertType mapAdvertRequestToUpdatedAdvertType(Long id, AdvertTypeRequest advertTypeRequest){
        return AdvertType.builder()
                .id(id)
                .title(advertTypeRequest.getTitle())
                .build();
    }
}
