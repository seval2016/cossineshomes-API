package com.project.payload.mappers;

import com.project.entity.concretes.business.Image;
import com.project.payload.response.business.ImageResponse;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class ImageMapper {

    public ImageResponse mapToImageResponse(Image image) {
        return ImageResponse.builder()
                .id(image.getId())
                .name(image.getName())
                .type(image.getType())
                .featured(image.isFeatured())
                .advertId(image.getAdvert().getId())
                .url("/images/" + image.getId())  // Assuming URL structure
                .build();
    }
}
