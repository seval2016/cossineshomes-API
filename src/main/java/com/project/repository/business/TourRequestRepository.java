package com.project.repository.business;

import com.project.entity.concretes.business.Advert;
import com.project.entity.concretes.business.TourRequest;
import com.project.entity.enums.TourRequestEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface TourRequestRepository extends JpaRepository<TourRequest, Long> {

    /**
     * S01 - Kullanıcıya ait tur taleplerini arama sorgusuna göre döndüren sorgu
     */
    @Query("SELECT t FROM TourRequest t WHERE t.guestUser.id = :userId AND " +
            "(t.advert.title LIKE %:query% OR t.tourDate LIKE %:query%)")
    Page<TourRequest> findAllByGuestUserIdAndQuery(@Param("userId") Long userId,
                                                   @Param("query") String query,
                                                   Pageable pageable);

    /**
     * S01 - Kullanıcının tüm tur taleplerini döndüren sorgu
     */
    Page<TourRequest> findAllByGuestUserId(Long userId, Pageable pageable);


    @Query("SELECT tr FROM TourRequest tr WHERE tr.guestUser.id = ?1")
    Page<TourRequest> findAllByUserId(Long id, Pageable pageable);

    @Query("SELECT tr FROM TourRequest tr WHERE ( tr.guestUser.id = ?1 ) AND ( tr.id = ?2 )")
    TourRequest findByIdForGuestUser(Long id, Long tourId);

    @Query("SELECT t FROM TourRequest t WHERE " +
            "(:date1 IS NULL OR :date2 IS NULL OR t.createAt BETWEEN :date1 AND :date2) AND " +
            "t.status = :statusType")
    List<TourRequest> getTourRequest(@Param("date1") LocalDateTime date1,
                                     @Param("date2") LocalDateTime date2,
                                     @Param("statusType") TourRequestEnum tourRequestEnum);

    @Query("SELECT t FROM TourRequest t WHERE t.guestUser.id = :userId AND " +
            "(LOWER(t.advert.title) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<TourRequest> findAllByGuestUserId_IdAndQuery(@Param("userId") Long userId, @Param("query") String query, Pageable pageable);

    Page<TourRequest> findAllByGuestUserId_Id(Long id, Pageable pageable);

    @Query("SELECT t FROM TourRequest t WHERE (:query IS NULL OR LOWER(t.advert.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(t.ownerUser.firstName) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<TourRequest> getTourRequestsByPageWithQuery(@Param("query") String query, Pageable pageable);

    Optional<TourRequest> findByIdAndGuestUserId_Id(Long id, Long id1);

    @Query("SELECT t FROM TourRequest t WHERE (t.guestUser.id=?1 OR  t.ownerUser.id=?1) AND t.id=?2")
    TourRequest findByIdByCustomer(Long userId, Long tourRequestId);

    Set<TourRequest> findByIdIn(Set<Long> tourRequestIdList);

    Optional<List<TourRequest>> findByAdvert(Advert advert);
}
