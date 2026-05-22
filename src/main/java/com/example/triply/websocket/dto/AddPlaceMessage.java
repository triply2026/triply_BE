package com.example.triply.websocket.dto;

import com.example.triply.tripPlan.entity.enums.PlaceCategory;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AddPlaceMessage {
    private Long memberId;
    private String nickname;
    private Long dayId;
    private String name;
    private String address;
    private Integer estimatedCost;
    private Integer stayDurationMin;
    private Double latitude;
    private Double longitude;
    private String mapPlaceId;
    private PlaceCategory category;
}
