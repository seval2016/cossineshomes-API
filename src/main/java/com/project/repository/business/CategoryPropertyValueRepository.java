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

    // Belirli bir advert'e ait tüm değerleri getir
    List<CategoryPropertyValue> findByAdvertAdvertId(Long advertId);

    // Belirli bir kategori özelliği anahtarına ait tüm değerleri getir
    List<CategoryPropertyValue> findByCategoryPropertyKey_Id(Long categoryPropertyKeyId);

    // Değerin içeriğine göre değerleri getir
    List<CategoryPropertyValue> findByValueContaining(String value);

    @Query("SELECT v FROM CategoryPropertyValue v WHERE value=?1")
    Optional<CategoryPropertyValue> findValueByName(Object obje);

    List<CategoryPropertyValue> findAllByValue(String value);

    @Modifying
    @Query("DELETE FROM CategoryPropertyValue")
    void deleteAllCategoryPropertyValuesExceptBuiltIn();
}
