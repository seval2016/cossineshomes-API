package com.project.payload.mappers;

import com.project.entity.concretes.business.AdvertType;
import com.project.payload.request.business.AdvertTypesRequest;
import com.project.payload.response.business.advert.AdvertTypesResponse;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class AdvertTypesMapper {
    //DTO-> POJO

    public AdvertType mapAdvertTypeRequestToAdvertType(AdvertTypesRequest advertTypesRequest){
        return AdvertType.builder()
                .title(advertTypesRequest.getTitle().toLowerCase())
                .build();
    }

    //POJO -> DTO

    public AdvertTypesResponse mapAdvertTypeToAdvertTypeResponse(AdvertType advertType){
        return AdvertTypesResponse.builder()
                .id(advertType.getId())
                .title(advertType.getTitle())
                .build();
    }

    //request -> updated pojo

    public AdvertType mapAdvertRequestToUpdatedAdvertType(Long id, AdvertTypesRequest advertTypesRequest){
        return AdvertType.builder()
                .id(id)
                .title(advertTypesRequest.getTitle())
                .build();
    }
}
