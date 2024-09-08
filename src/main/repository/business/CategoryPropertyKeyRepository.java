package com.project.repository.business;

import com.project.entity.concretes.business.CategoryPropertyKey;
import com.project.entity.concretes.business.CategoryPropertyValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryPropertyKeyRepository extends JpaRepository<CategoryPropertyKey, Long> {

    @Modifying
    @Query("DELETE FROM CategoryPropertyKey apk WHERE apk.builtIn=false")
    void deleteAllCategoryPropertyKeysExceptBuiltIn();
}
