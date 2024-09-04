package com.project.repository.business;

import com.project.entity.concretes.business.TourRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TourRequestRepository extends JpaRepository<TourRequest,Long> {

    void deleteByAdvertId(Long id);
}
