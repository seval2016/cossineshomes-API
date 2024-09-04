package com.project.repository.business;

import com.project.repository.business.entity.concretes.business.City;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CityRepository extends JpaRepository<City, Long> {
}
