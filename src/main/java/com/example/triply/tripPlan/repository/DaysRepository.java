package com.example.triply.tripPlan.repository;

import com.example.triply.tripPlan.entity.Days;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DaysRepository extends JpaRepository<Days, Long> {
    List<Days> findByPlanIdOrderByDayNumber(Long planId);
}
