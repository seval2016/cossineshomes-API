package com.project.repository.business;

import com.project.entity.concretes.business.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteRepository extends JpaRepository<Favorite,Long> {
    void deleteByAdvertId(Long id);
}
