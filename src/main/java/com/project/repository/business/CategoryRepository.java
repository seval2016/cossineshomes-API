package com.project.repository.business;

import com.project.repository.business.entity.concretes.business.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
