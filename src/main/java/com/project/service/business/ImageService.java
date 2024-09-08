package com.project.service.business;

import com.project.entity.concretes.business.Advert;
import com.project.entity.concretes.business.Image;
import com.project.payload.mappers.ImageMapper;
import com.project.payload.response.business.ImageResponse;

import com.project.repository.business.AdvertRepository;
import com.project.repository.business.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;
    private final AdvertRepository advertRepository;
    private final ImageMapper imageMapper;

    public ImageResponse getImageById(Long imageId) {
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new EntityNotFoundException("Image not found"));
        return imageMapper.mapToImageResponse(image);
    }

    public List<Long> uploadImages(Long advertId, List<MultipartFile> files) throws IOException {
        Advert advert = advertRepository.findById(advertId)
                .orElseThrow(() -> new EntityNotFoundException("Advert not found"));

        List<Long> imageIds = new ArrayList<>();
        for (MultipartFile file : files) {
            Image image = Image.builder()
                    .name(file.getOriginalFilename())
                    .type(file.getContentType())
                    .data(file.getBytes())
                    .advert(advert)
                    .build();
            Image savedImage = imageRepository.save(image);
            imageIds.add(savedImage.getId());
        }
        return imageIds;
    }

    public void deleteImages(List<Long> imageIds) {
        imageRepository.deleteByIdIn(imageIds);
    }

    public ImageResponse setFeaturedImage(Long imageId) {
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new EntityNotFoundException("Image not found"));

        List<Image> advertImages = imageRepository.findByAdvertId(image.getAdvert().getId());

        // Unset all other images as featured
        advertImages.forEach(img -> {
            if (img.isFeatured()) {
                img.setFeatured(false);
                imageRepository.save(img);
            }
        });

        // Set this image as featured
        image.setFeatured(true);
        imageRepository.save(image);

        return imageMapper.mapToImageResponse(image);
    }

}
