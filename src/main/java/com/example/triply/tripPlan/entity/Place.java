package com.example.triply.tripPlan.entity;

import com.example.triply.member.entity.Member;
import com.example.triply.tripPlan.entity.enums.PlaceCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "place")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Place {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "day_id", nullable = false)
    private Days day;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Member member;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 200)
    private String address;

    @Column(name = "estimated_cost")
    private Integer estimatedCost;

    @Column(name = "stay_duration_min")
    private Integer stayDurationMin;

    private Double latitude;

    private Double longitude;

    @Column(name = "map_place_id", length = 50)
    private String mapPlaceId;

    @Enumerated(EnumType.STRING)
    private PlaceCategory category;

    @Column(name = "order_index")
    private Integer orderIndex;
}
