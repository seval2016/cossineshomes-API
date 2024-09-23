package com.project.service.helper;

import com.project.entity.concretes.business.Advert;
import com.project.entity.concretes.business.Image;
import com.project.payload.response.business.image.ImageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ImageHelper {

    private Image getFeaturedImage(List<Image> images) {
        return images.stream()
                .filter(Image::getFeatured)
                .findFirst()
                .orElse(images.get(0));
    }

}
