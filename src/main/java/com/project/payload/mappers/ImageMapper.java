package com.project.payload.mappers;

import com.project.entity.concretes.business.Image;
import com.project.payload.response.business.ImageResponse;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Data
@Component
public class ImageMapper {

    public ImageResponse mapToImageResponse(Image image) {
        return ImageResponse.builder()
                .id(image.getId())
                .name(image.getName())
                .type(image.getType())
                .featured(image.isFeatured())
                .data(encodeImage(image.getData()).getBytes())
                .advertId(image.getAdvert().getId())
                .build();
    }
    private String encodeImage(byte[] imageData) {
        return Base64.getEncoder().encodeToString(imageData);
    }
}
