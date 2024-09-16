package com.project.repository.business;

import com.project.entity.concretes.business.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findBySlug(String slug);

    @Query("SELECT c FROM Category c WHERE LOWER(c.title) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Category> findByTitleContaining(@Param("query") String query, Pageable pageable);

    @Query("SELECT c FROM Category c WHERE LOWER(c.title) LIKE LOWER(CONCAT('%', :title, '%')) AND c.isActive = TRUE")
    Page<Category> findByTitleContainingAndIsActiveTrue(@Param("title") String title, Pageable pageable);

    boolean existsByTitle(String title);

    boolean existsBySlug(String slug);

    Optional<List<Category>> findByTitle(String title);
}
