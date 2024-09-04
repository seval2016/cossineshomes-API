package com.project.repository.business;

import com.project.repository.business.entity.concretes.business.District;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DistrictRepository extends JpaRepository<District, Long> {
}
