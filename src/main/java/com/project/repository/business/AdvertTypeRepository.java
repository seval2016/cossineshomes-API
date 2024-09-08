package com.project.repository.business;

import com.project.entity.concretes.business.AdvertType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AdvertTypeRepository extends JpaRepository<AdvertType, Long> {

    @Modifying
    @Query("DELETE FROM AdvertType a_type WHERE a_type.builtIn = false")
    void deleteAllAdvertTypesExceptBuiltIn();
}
