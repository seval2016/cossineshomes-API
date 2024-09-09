package com.project.repository.business;

import com.project.entity.concretes.business.TourRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TourRequestRepository extends JpaRepository<TourRequest,Long> {

    @Query("SELECT tr FROM TourRequest tr WHERE tr.guestUser.id = ?1")
    Page<TourRequest> findAllByUserId(Long id, Pageable pageable);

    @Query("SELECT tr FROM TourRequest tr WHERE ( tr.guestUser.id = ?1 ) AND ( tr.id = ?2 )")
    TourRequest findByIdForGuestUser(Long id, Long tourId);

    @Modifying
    @Query("DELETE FROM TourRequest")
    void deleteAllTourRequestsExceptBuiltIn();

}
