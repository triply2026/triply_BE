package com.example.triply.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItineraryResponseDto {

    private List<DayPlan> days;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DayPlan {
        private int dayNumber;
        private LocalDate date;
        private List<PlacePlan> places;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlacePlan {
        private String name;
        private String address;
        private String category;
        private Integer estimatedCost;
        private Integer stayDurationMin;
        private String description;
    }
}
