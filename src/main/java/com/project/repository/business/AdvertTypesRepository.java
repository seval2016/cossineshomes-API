package com.project.repository.business;

import com.project.entity.concretes.business.AdvertType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdvertTypesRepository extends JpaRepository<AdvertType, Long> {
    boolean existsByTitle(String title);

    Optional<AdvertType>  findByTitle(String type);
}
