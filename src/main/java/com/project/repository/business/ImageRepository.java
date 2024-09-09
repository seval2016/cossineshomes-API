package com.project.repository.business;

import com.project.entity.concretes.business.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {

    List<Image> findByAdvertId(Long advertId);

    Optional<Image> findById(Long id);

    void deleteByIdIn(List<Long> imageIds);

    @Query("SELECT i FROM Image i WHERE i.advert.id = :advertId AND i.featured = true")
    Optional<Image> findFeaturedImageByAdvertId(@Param("advertId") Long advertId);

    @Modifying
    @Query("DELETE FROM Image i WHERE i.featured = false")
    void deleteAllImagesExceptBuiltIn();
}

