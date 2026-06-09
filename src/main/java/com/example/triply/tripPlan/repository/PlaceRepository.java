package com.example.triply.tripPlan.repository;

import com.example.triply.tripPlan.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PlaceRepository extends JpaRepository<Place, Long> {
    List<Place> findByDayIdOrderByOrderIndex(Long dayId);

    List<Place> findByDayIdIn(List<Long> dayIds);
    void deleteByDayIdIn(List<Long> dayIds);

    @Modifying
    @Query("UPDATE Place p SET p.orderIndex = :orderIndex WHERE p.id = :placeId")
    void updateOrderIndex(@Param("placeId") Long placeId, @Param("orderIndex") Integer orderIndex);

    @Query("SELECT p.day.plan.id FROM Place p WHERE p.id = :placeId")
    Optional<Long> findPlanIdByPlaceId(@Param("placeId") Long placeId);
}
