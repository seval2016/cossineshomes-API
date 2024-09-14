package com.project.repository.business;


import com.project.entity.concretes.business.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {
    List<City> findByCountryId(Long countryId);

    @Query("SELECT COUNT(c) FROM City c")
    int countAllCities();
}
