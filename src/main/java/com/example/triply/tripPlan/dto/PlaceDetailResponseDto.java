package com.example.triply.tripPlan.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class PlaceDetailResponseDto {

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Detail {
        private Long placeId;
        private String name;
        private String category;
        private String description;
        private Integer estimatedDuration;
        private Integer estimatedCost;
        private String memo;
        private String reservationUrl;
        private String address;
        private List<String> sourceUrls;
        private List<String> images;
        private long likeCount;
        private long dislikeCount;
        private String myVoteType;
    }
}
