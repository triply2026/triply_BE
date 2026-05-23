package com.example.triply.tripPlan.entity;

import com.example.triply.tripPlan.entity.enums.PlanStatus;
import com.example.triply.tripPlan.entity.enums.TripStyle;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "plan")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String title;

    @Column(nullable = false, length = 40)
    private String destination;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "member_count")
    private Integer memberCount;

    private Integer budget;

    @Enumerated(EnumType.STRING)
    @Column(name = "trip_style")
    private TripStyle tripStyle;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlanStatus status;

    @Column(name = "shared_token", unique = true, length = 100)
    private String sharedToken;
}
