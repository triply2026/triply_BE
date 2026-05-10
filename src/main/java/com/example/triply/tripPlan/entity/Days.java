package com.example.triply.tripPlan.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "day")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Days {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;

    @Column(name = "day_number", nullable = false)
    private Integer dayNumber;

    @Column(nullable = false)
    private LocalDate date;
}
