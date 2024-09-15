package com.project.repository.business;

import com.project.entity.concretes.business.CategoryPropertyKey;
import com.project.entity.concretes.business.CategoryPropertyValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;

@Repository
public interface CategoryPropertyKeyRepository extends JpaRepository<CategoryPropertyKey, Long> {

    List<CategoryPropertyKey> findByCategoryId(Long categoryId);

    List<CategoryPropertyKey> findByName(String name);

    @Transactional
    @Modifying
    @Query("DELETE FROM CategoryPropertyKey c WHERE c.builtIn = ?1")
    void deleteByBuiltIn(boolean b);

    boolean existsByName(String name);

    Set<CategoryPropertyKey> findByCategory_Id(Long id);
}
