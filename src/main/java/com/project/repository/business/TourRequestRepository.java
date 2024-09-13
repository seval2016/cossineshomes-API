package com.project.repository.business;

import com.project.entity.concretes.business.TourRequest;
import com.project.entity.enums.StatusType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TourRequestRepository extends JpaRepository<TourRequest,Long> {

    @Query("SELECT tr FROM TourRequest tr WHERE tr.guestUser.id = ?1")
    Page<TourRequest> findAllByUserId(Long id, Pageable pageable);

    @Query("SELECT tr FROM TourRequest tr WHERE ( tr.guestUser.id = ?1 ) AND ( tr.id = ?2 )")
    TourRequest findByIdForGuestUser(Long id, Long tourId);

    /*
    @Modifying
    @Query("DELETE FROM TourRequest")
    void deleteAllTourRequestsExceptBuiltIn();
    */

    @Query("SELECT t FROM TourRequest t WHERE " +
            "(:date1 IS NULL OR :date2 IS NULL OR t.createAt BETWEEN :date1 AND :date2) AND " +
            "t.status = :statusType")
    List<TourRequest> getTourRequest(@Param("date1") LocalDateTime date1,
                                     @Param("date2") LocalDateTime date2,
                                     @Param("statusType") StatusType statusType);

}
