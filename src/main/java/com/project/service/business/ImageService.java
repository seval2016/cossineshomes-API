package com.project.service.business;

import com.project.entity.concretes.business.Advert;
import com.project.entity.concretes.business.Images;
import com.project.exception.ResourceNotFoundException;
import com.project.payload.mappers.ImagesMapper;
import com.project.payload.messages.ErrorMessages;
import com.project.payload.response.business.ImagesResponse;

import com.project.repository.business.AdvertRepository;
import com.project.repository.business.ImagesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImagesRepository imagesRepository;
    private final AdvertRepository advertRepository;
    private final ImagesMapper imagesMapper;

    private final String imageDirectory="/path/to/image/directory";
    public List<Images> getALlImages(){
        return imagesRepository.findAll();
    }

    public byte[] updateFeaturedOfImage(Long imageId) {

        Images image = imagesRepository.findById(imageId).orElseThrow(()->
                new ResourceNotFoundException(ErrorMessages.IMAGE_NOT_FOUND));

        List<Images> images = imagesRepository.findByAdvertId(image.getAdvert().getId());

        images.forEach(item -> item.setFeatured(false));

        image.setFeatured(true);

        imagesRepository.save(image);

        return image.getData();
    }

    public ImagesResponse getImageById(Long imageId) {
        Images images = imagesRepository.findById(imageId)
                .orElseThrow(() -> new EntityNotFoundException("Image not found"));
        return imagesMapper.mapToImageResponse(images);
    }

    public List<Long> uploadImages(Long advertId, List<MultipartFile> files) throws IOException {
        Advert advert = advertRepository.findById(advertId)
                .orElseThrow(() -> new EntityNotFoundException("Advert not found"));

        List<Long> imageIds = new ArrayList<>();
        for (MultipartFile file : files) {
            Images images = Images.builder()
                    .name(file.getOriginalFilename())
                    .type(file.getContentType())
                    .data(file.getBytes())
                    .advert(advert)
                    .build();
            Images savedImages = imagesRepository.save(images);
            imageIds.add(savedImages.getId());
        }
        return imageIds;
    }

    public void deleteImages(List<Long> imageIds) {


        List<Images> images = imagesRepository.findAllById(imageIds);

        for (Images image:images) {
            if(image==null){
                throw new ResourceNotFoundException(ErrorMessages.IMAGE_NOT_FOUND);
            }

            //Imageleri fiziksel silmek için
            Path imagePath = Paths.get(imageDirectory,image.getName());
            try {
                Files.deleteIfExists(imagePath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        imagesRepository.deleteAllById(imageIds);
    }

    public ImagesResponse setFeaturedImage(Long imageId) {
        Images images = imagesRepository.findById(imageId)
                .orElseThrow(() -> new EntityNotFoundException("Image not found"));

        List<Images> advertImages = imagesRepository.findByAdvertId(images.getAdvert().getId());

        // Unset all other images as featured
        advertImages.forEach(img -> {
            if (img.isFeatured()) {
                img.setFeatured(false);
                imagesRepository.save(img);
            }
        });

        // Set this image as featured
        images.setFeatured(true);
        imagesRepository.save(images);

        return imagesMapper.mapToImageResponse(images);
    }

    public void resetImageTables() {
        imagesRepository.deleteAll();
    }

    public String convertToBase64(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }
}
