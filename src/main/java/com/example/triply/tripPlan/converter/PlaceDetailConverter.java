package com.example.triply.tripPlan.converter;

import com.example.triply.tripPlan.dto.PlaceDetailResponseDto;
import com.example.triply.tripPlan.entity.Place;
import com.example.triply.tripPlan.entity.PlaceDetail;

public class PlaceDetailConverter {

    private PlaceDetailConverter() {
    }

    public static PlaceDetailResponseDto.Detail toDetail(Place place, PlaceDetail placeDetail) {
        return PlaceDetailResponseDto.Detail.builder()
                .placeId(place.getId())
                .name(place.getName())
                .category(place.getCategory() != null ? place.getCategory().name() : null)
                .description(placeDetail != null ? placeDetail.getDescription() : null)
                .estimatedDuration(place.getStayDurationMin())
                .estimatedCost(place.getEstimatedCost())
                .memo(place.getMemo())
                .reservationUrl(place.getReservationUrl())
                .address(place.getAddress())
                .build();
    }
}
