package com.project.controller.business;

import com.project.payload.response.business.ImagesResponse;
import com.project.service.business.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
public class ImageController {
    private final ImageService imageService;

    @GetMapping("/{imageId}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<byte[]> getImageById(@PathVariable Long imageId) {
        ImagesResponse imagesResponse = imageService.getImageById(imageId);
        byte[] imageData = imageService.getImageById(imageId).getData();
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(imageData);
    }

    @PostMapping("/{advertId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN', 'MANAGER')")
    public ResponseEntity<List<Long>> uploadImages(@PathVariable Long advertId,
                                                   @RequestParam("files") List<MultipartFile> files) throws IOException {
        List<Long> imageIds = imageService.uploadImages(advertId, files);
        return ResponseEntity.status(HttpStatus.CREATED).body(imageIds);
    }

    @DeleteMapping("/{imageIds}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN', 'MANAGER')")
    public ResponseEntity<Void> deleteImages(@PathVariable List<Long> imageIds) {
        imageService.deleteImages(imageIds);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{imageId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN', 'MANAGER')")
    public ResponseEntity<ImagesResponse> setFeaturedImage(@PathVariable Long imageId) {
        ImagesResponse featuredImage = imageService.setFeaturedImage(imageId);
        return ResponseEntity.ok(featuredImage);
    }
}
