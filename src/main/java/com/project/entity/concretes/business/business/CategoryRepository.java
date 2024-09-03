package com.project.entity.concretes.business.business;

import com.project.entity.concretes.business.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
