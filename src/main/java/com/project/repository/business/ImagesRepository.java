package com.project.repository.business;

import com.project.entity.concretes.business.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImagesRepository extends JpaRepository<Image, Long> {

    List<Image> findByAdvertId(Long advertId);


}

