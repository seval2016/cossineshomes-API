package com.project.repository.business;

import com.project.entity.concretes.business.Advert;
import com.project.entity.concretes.user.User;
import com.project.entity.enums.AdvertStatus;
import com.project.payload.response.business.advert.PopularAdvertResponse;
import com.project.payload.response.business.category.CategoryAdvertResponse;
import com.project.payload.response.business.advert.CityAdvertResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AdvertRepository extends JpaRepository<Advert, Long> {

    // --> A01 - Belirli filtreleme kriterlerine göre ilanları getirir.
    @Query("SELECT a FROM Advert a " +
            "WHERE (:q IS NULL OR (a.title LIKE %:q% OR a.desc LIKE %:q%)) " + // q parametresi title veya desc içinde aranır
            "AND (:categoryId IS NULL OR a.category.id = :categoryId) " + // categoryId filtrelemesi
            "AND (:advertTypeId IS NULL OR a.advertType.id = :advertTypeId) " + // advertTypeId filtrelemesi
            "AND (:priceStart IS NULL OR a.price >= :priceStart) " + // priceStart (minimum fiyat) filtrelemesi
            "AND (:priceEnd IS NULL OR a.price <= :priceEnd) " + // priceEnd (maximum fiyat) filtrelemesi
            "AND (:status IS NULL OR a.status = :status) " + // status parametresi ile ilan durumu
            "AND a.isActive = true") // sadece aktif ilanları getir
    Page<Advert> findByAdvertByPage(
            String q, // arama metni
            Long categoryId, // kategori ID'si
            Long advertTypeId, // ilan tipi ID'si
            BigDecimal priceStart, // minimum fiyat
            BigDecimal priceEnd, // maksimum fiyat
            Integer status, // ilan durumu
            Pageable pageable // sayfalama ve sıralama parametreleri
    );

    //A02
    @Query("SELECT c.name AS cityName, COUNT(a) AS amount " +
            "FROM City c " +
            "LEFT JOIN Advert a ON a.city.id = c.id " +
            "GROUP BY c.id, c.name")
    List<CityAdvertResponse> findAdvertsGroupedByCities();


    /**
     * A03 - Kategorilere göre ilan sayısını döner.
     *
     * @return List of CategoryAdvertResponse
     */
    @Query("SELECT c.name AS categoryName, COUNT(a) AS amount" +
            "FROM Category c " +
            "LEFT JOIN Advert a ON a.category.id = c.id " +
            "GROUP BY c.id, c.name")
    List<CategoryAdvertResponse> findAdvertsGroupedByCategories();

    /**
     * A04 - Popüler ilanları döner.
     * Popülerlik puanı: PP = 3 * TROA + TVOA
     *
     * @param pageable Pageable nesnesi ile limit belirlenir.
     * @return List of PopularAdvertResponse
     */
    @Query("SELECT a.id AS id, " +
            "a.title AS title, " +
            "a.slug AS slug, " +
            "a.price AS price, " +
            "a.viewCount AS viewCount, " +
            "COUNT(tr) AS tourRequestCount, " +
            "(3 * COUNT(tr) + a.viewCount) AS popularity " +
            "FROM Advert a " +
            "LEFT JOIN a.tourRequestList tr " +
            "GROUP BY a.id, a.title, a.slug, a.price, a.viewCount " +
            "ORDER BY (3 * COUNT(tr) + a.viewCount) DESC")
    List<PopularAdvertResponse> findPopularAdverts(Pageable pageable);

    //A06
    @Query("SELECT a FROM Advert a WHERE " +
            "(:query IS NULL OR a.description LIKE %:query% OR a.title LIKE %:query% ) " +
            "AND (:categoryId IS NULL OR a.category.id = :categoryId) " +
            "AND (:advertTypeId IS NULL OR a.advertType.id = :advertTypeId) " +
            "AND (:priceStart IS NULL OR a.price >= :priceStart) " +
            "AND (:priceEnd IS NULL OR a.price <= :priceEnd)" +
            "AND (:status IS NULL OR a.status = :status)")
    Page<Advert> findByAdvertByQuery(
            @Param("query") String query,
            @Param("categoryId") Long categoryId,
            @Param("advertTypeId") Long advertTypeId,
            @Param("priceStart") Double priceStart,
            @Param("priceEnd") Double priceEnd,
            @Param("status")Integer status,
            Pageable pageable
    );


    //A07
    Optional<Advert> findBySlug(String slug);

    @Query("SELECT a FROM Advert a WHERE a.user.id = :userId")
    Page<Advert> findAdvertsForUser(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT a FROM Advert a WHERE (:date1 IS NULL OR :date2 IS NULL OR  a.createAt BETWEEN:date1 AND :date2) AND " +
            "(:category IS NULL OR a.category.title =:category) AND (:type IS NULL OR a.advertType.title=:type) AND " +
            "(:enumStatus IS NULL OR a.status=:enumStatus)")
    Optional<List<Advert>> findByQuery(@Param(value = "date1") LocalDateTime date1,
                                       @Param(value = "date2") LocalDateTime date2,
                                       @Param(value = "category") String category,
                                       @Param(value = "type") String type,
                                       @Param(value = "enumStatus") AdvertStatus enumStatus);


    Page<Advert> findByUser(User user, Pageable pageable);
}