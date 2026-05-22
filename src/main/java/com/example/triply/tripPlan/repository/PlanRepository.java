package com.example.triply.tripPlan.repository;

import com.example.triply.tripPlan.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlanRepository extends JpaRepository<Plan, Long> {
    Optional<Plan> findBySharedToken(String sharedToken);
}
