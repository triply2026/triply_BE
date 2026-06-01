package com.example.triply.tripPlan.dto;

import lombok.Getter;

public class PlaceDetailRequestDto {

    @Getter
    public static class Update {
        private Integer estimatedDuration;
        private Integer estimatedCost;
        private String memo;
        private String reservationUrl;
    }
}
