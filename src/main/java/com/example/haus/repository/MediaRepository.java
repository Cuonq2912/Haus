package com.example.haus.repository;

import com.example.haus.domain.entity.product.Media;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MediaRepository extends JpaRepository<Media, Long> {

    @Query("SELECT m FROM medias m WHERE m.id IN :ids AND m.product.id = :productId")
    List<Media> findByIdsAndProductId(@Param("ids") List<Long> ids, @Param("productId") Long productId);

}
