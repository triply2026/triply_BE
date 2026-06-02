package com.example.triply.tripPlan.repository;

import com.example.triply.tripPlan.entity.PlaceDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlaceDetailRepository extends JpaRepository<PlaceDetail, Long> {
    Optional<PlaceDetail> findByPlaceId(Long placeId);
    void deleteByPlaceId(Long placeId);
}
