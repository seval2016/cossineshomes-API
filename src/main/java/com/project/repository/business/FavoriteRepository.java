package com.project.repository.business;

import com.project.entity.concretes.business.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

public interface FavoriteRepository extends JpaRepository<Favorite,Long> {
    @Query("SELECT f FROM Favorite f WHERE f.id = :id")
    List<Favorite> findFavorite(@Param("id") Long id);

    @Modifying
    @Query("DELETE FROM Favorite f WHERE f.user.id = :user_id AND f.advert.id = :advert_id")
    void deleteFavoriteIfExists(@Param("user_id") Long userId, @Param("advert_id") Long advertId);

    @Transactional
    @Modifying
    @Query("INSERT INTO Favorite (user, advert) SELECT u, a FROM User u, Advert a WHERE u.id = :user_id AND a.id = :advert_id")
    void addFavoriteIfNotExists(@Param("user_id") Long userId, @Param("advert_id") Long advertId);

    @Query("SELECT COUNT(f) > 0 FROM Favorite f WHERE f.user.id = :userId AND f.advert.id = :advertId")
    boolean existsByUserIdAndAdvertId(@Param("userId") Long userId, @Param("advertId") Long advertId);

    @Query("SELECT f FROM Favorite f WHERE f.user.id = :user_id")
    List<Favorite> findFavoritesByUserId(@Param("user_id") Long userId);

    @Modifying
    @Query("DELETE FROM Favorite f WHERE f.user.id = :user_id")
    void deleteAllByUserId(@Param("user_id") Long userId);

    @Query("SELECT f FROM Favorite f WHERE f.user.id=?1 AND f.advert.id=?2")
    Favorite getFavoriteByAdvertAndUser(Long id, Long advertId);

    List<Favorite> findAllByAdvert_Id(Long advertId);

    @Modifying
    @Query("DELETE FROM Favorite f WHERE f.builtIn = false")
    void deleteAllFavoritesExceptBuiltIn();
}
