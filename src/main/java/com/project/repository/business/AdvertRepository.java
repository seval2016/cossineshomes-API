package com.project.repository.business;

import com.project.entity.concretes.business.Advert;
import com.project.entity.enums.AdvertStatus;
import com.project.payload.response.abstracts.BaseUserResponse;
import com.project.payload.response.business.CityAdvertResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.DoubleStream;

@Repository
public interface AdvertRepository extends JpaRepository<Advert, Long> {

    @Query("SELECT a FROM Advert a WHERE " +
            "(:q IS NULL OR (LOWER(a.title) LIKE LOWER(CONCAT('%', :q, '%')) " +
            "OR LOWER(a.description) LIKE LOWER(CONCAT('%', :q, '%')))) " +
            "AND (:categoryId IS NULL OR a.category.id = :categoryId) " +
            "AND (:advertTypeId IS NULL OR a.advertType.id = :advertTypeId) " +
            "AND (:priceStart IS NULL OR a.price >= :priceStart) " +
            "AND (:priceEnd IS NULL OR a.price <= :priceEnd) " +
            "AND (:status IS NULL OR a.status = :status) " +
            "AND a.isActive = true")
    Page<Advert> findAdverts(@Param("q") String q,
                             @Param("categoryId") Long categoryId,
                             @Param("advertTypeId") Long advertTypeId,  // Long tipine dönüştürüldü
                             @Param("priceStart") BigDecimal priceStart,
                             @Param("priceEnd") BigDecimal priceEnd,
                             @Param("status") Integer status,
                             Pageable pageable);


    @Query("SELECT c.name AS cityName, COUNT(a) AS amount " +
            "FROM City c " +
            "LEFT JOIN Advert a ON a.city.id = c.id " +
            "GROUP BY c.id, c.name")
    List<CityAdvertResponse> findAdvertsGroupedByCities();

    @Query("SELECT a FROM Advert a ORDER BY (3 * SIZE(a.tourRequestList) + a.viewCount) DESC")
    Page<Advert> findMostPopularAdverts(Pageable pageable);

    Page<Advert> findByUser(BaseUserResponse currentUser, Pageable pageable);

    @Modifying
    @Query("DELETE FROM Advert a WHERE a.builtIn=false")
    void deleteAllAdvertsExceptBuiltIn();


    @Query("SELECT a FROM Advert a WHERE " +
            "(LOWER(a.title) LIKE LOWER(CONCAT('%', :q, '%')) OR LOWER(a.description) LIKE LOWER(CONCAT('%', :q, '%'))) " +
            "AND (:categoryId IS NULL OR a.category.id = :categoryId) " +
            "AND (:advertTypeId IS NULL OR a.advertType.id = :advertTypeId) " +
            "AND (:priceStart IS NULL OR a.price >= :priceStart) " +
            "AND (:priceEnd IS NULL OR a.price <= :priceEnd) " +
            "AND (:status IS NULL OR a.status = :status)")
    Page<Advert> findAdvertsByCriteria(
            @Param("q") String q,
            @Param("categoryId") Long categoryId,
            @Param("advertTypeId") Long advertTypeId,
            @Param("priceStart") Double priceStart,
            @Param("priceEnd") Double priceEnd,
            @Param("status") Integer status,
            Pageable pageable
    );

    Optional<Advert> findBySlug(String slug);

    Optional<Advert> findByIdAndUserId(Long id, Long id1);

    @Query("SELECT a FROM Advert a WHERE a.user.id= ?1 ")
    Page<Advert> findAdvertsForUser(Long id, Pageable pageable);

    @Query("SELECT a FROM Advert a WHERE (:date1 IS NULL OR :date2 IS NULL OR  a.createAt BETWEEN:date1 AND :date2) AND " +
            "(:category IS NULL OR a.category.title =:category) AND (:type IS NULL OR a.advertType.title=:type) AND " +
            "(:enumStatus IS NULL OR a.status=:enumStatus)")
    Optional<List<Advert>> findByQuery(@Param(value = "date1") LocalDateTime date1,
                                       @Param(value = "date2") LocalDateTime date2,
                                       @Param(value = "category")String category,
                                       @Param(value = "type") String type,
                                       @Param(value = "enumStatus") AdvertStatus enumStatus);
}