package com.project.repository.business;

import com.project.entity.concretes.business.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
    void deleteByAdvertId(Long advertId);
}
