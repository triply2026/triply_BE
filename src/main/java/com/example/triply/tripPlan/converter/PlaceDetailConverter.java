package com.example.triply.tripPlan.converter;

import com.example.triply.tripPlan.dto.PlaceDetailResponseDto;
import com.example.triply.tripPlan.entity.Place;
import com.example.triply.tripPlan.entity.PlaceDetail;
import com.example.triply.vote.dto.VoteResponseDto;

public class PlaceDetailConverter {

    private PlaceDetailConverter() {
    }

    public static PlaceDetailResponseDto.Detail toDetail(Place place, PlaceDetail placeDetail, VoteResponseDto.VoteSummary voteSummary) {
        return PlaceDetailResponseDto.Detail.builder()
                .placeId(place.getId())
                .name(place.getName())
                .category(place.getCategory() != null ? place.getCategory().name() : null)
                .description(placeDetail != null ? placeDetail.getDescription() : null)
                .sourceUrls(placeDetail != null ? placeDetail.getSourceUrls() : null)
                .images(placeDetail != null ? placeDetail.getImage() : null)
                .estimatedDuration(place.getStayDurationMin())
                .estimatedCost(place.getEstimatedCost())
                .memo(place.getMemo())
                .reservationUrl(place.getReservationUrl())
                .address(place.getAddress())
                .likeCount(voteSummary != null ? voteSummary.getLikeCount() : 0)
                .dislikeCount(voteSummary != null ? voteSummary.getDislikeCount() : 0)
                .myVoteType(voteSummary != null && voteSummary.getMyVoteType() != null ? voteSummary.getMyVoteType().name() : null)
                .build();
    }
}
