package com.project.repository.business;

import com.project.entity.concretes.business.Log;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogRepository extends JpaRepository<Log, Long> {
    void deleteByAdvertId(Long advertId);
}