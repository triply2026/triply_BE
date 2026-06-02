package com.example.triply.tripPlan.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;

@Entity
@Table(name = "place_detail")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaceDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", nullable = false, unique = true)
    private Place place;

    @Column(columnDefinition = "TEXT")
    private String description;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "source_urls", columnDefinition = "jsonb")
    private List<String> sourceUrls;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<String> image;

    public void updateFromAi(String description, List<String> sourceUrls, List<String> images) {
        this.description = description;
        this.sourceUrls = sourceUrls;
        this.image = images;
    }
}
