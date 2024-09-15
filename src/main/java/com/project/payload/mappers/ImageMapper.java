package com.project.payload.mappers;

import com.project.entity.concretes.business.Images;
import com.project.payload.response.business.ImagesResponse;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Data
@Component
public class ImageMapper {

    public ImagesResponse mapToImageResponse(Images images) {
        return ImagesResponse.builder()
                .id(images.getId())
                .name(images.getName())
                .type(images.getType())
                .featured(images.isFeatured())
                .data(encodeImage(images.getData()).getBytes())
                .advertId(images.getAdvert().getId())
                .build();
    }
    private String encodeImage(byte[] imageData) {
        return Base64.getEncoder().encodeToString(imageData);
    }
}
