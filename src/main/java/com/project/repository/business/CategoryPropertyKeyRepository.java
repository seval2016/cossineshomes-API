package com.project.repository.business;

import com.project.entity.concretes.business.CategoryPropertyKey;
import com.project.entity.concretes.business.CategoryPropertyValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface CategoryPropertyKeyRepository extends JpaRepository<CategoryPropertyKey, Long> {

    // Belirli bir kategoriye ait tüm anahtarları getir
    List<CategoryPropertyKey> findByCategoryId(Long categoryId);

    // Belirli bir yöneticinin tüm anahtarlarını getir
    List<CategoryPropertyKey> findByManagerId(Long managerId);

    // Anahtar adını kullanarak anahtar bul
    List<CategoryPropertyKey> findByName(String name);
    @Modifying
    @Query("DELETE FROM CategoryPropertyKey apk WHERE apk.builtIn=false")
    void deleteAllCategoryPropertyKeysExceptBuiltIn();

    Set<CategoryPropertyKey> findByCategory_Id(Long id);
}
