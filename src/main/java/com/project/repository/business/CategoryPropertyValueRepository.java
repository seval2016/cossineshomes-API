package com.project.repository.business;

import com.project.entity.concretes.business.CategoryPropertyValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryPropertyValueRepository extends JpaRepository<CategoryPropertyValue, Long> {

    List<CategoryPropertyValue> findByAdvertId(Long advertId);

    List<CategoryPropertyValue> findByCategoryPropertyKey_Id(Long categoryPropertyKeyId);

    List<CategoryPropertyValue> findByValueContaining(String value);

    @Query("SELECT v FROM CategoryPropertyValue v WHERE value=?1")
    Optional<CategoryPropertyValue> findValueByName(Object obje);

    List<CategoryPropertyValue> findAllByValue(String value);

    // Hatalı alan ismi düzeltildi
    void deleteByCategoryPropertyKey_Id(Long id);
}

