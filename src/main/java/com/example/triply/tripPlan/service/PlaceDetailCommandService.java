package com.example.triply.tripPlan.service;

import com.example.triply.tripPlan.converter.PlaceDetailConverter;
import com.example.triply.tripPlan.dto.PlaceDetailRequestDto;
import com.example.triply.tripPlan.dto.PlaceDetailResponseDto;
import com.example.triply.tripPlan.entity.Place;
import com.example.triply.tripPlan.entity.PlaceDetail;
import com.example.triply.tripPlan.repository.PlaceDetailRepository;
import com.example.triply.tripPlan.repository.PlaceRepository;
import com.example.triply.vote.service.VoteQueryService;
import com.example.triply.websocket.dto.EditSaveMessage;
import com.example.triply.websocket.dto.PlanEventMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class PlaceDetailCommandService {

    private final PlaceRepository placeRepository;
    private final PlaceDetailRepository placeDetailRepository;
    private final VoteQueryService voteQueryService;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public PlaceDetailResponseDto.Detail updatePlace(Long placeId, PlaceDetailRequestDto.Update request, Long memberId) {
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 장소입니다."));
        place.update(request.getEstimatedDuration(), request.getEstimatedCost(),
                request.getMemo(), request.getReservationUrl());
        PlaceDetail placeDetail = placeDetailRepository.findByPlaceId(placeId).orElse(null);

        placeRepository.findPlanIdByPlaceId(placeId).ifPresent(planId ->
                messagingTemplate.convertAndSend("/topic/plan/" + planId,
                        PlanEventMessage.of(
                                PlanEventMessage.EventType.PLACE_UPDATED,
                                planId, memberId, null,
                                Map.of(
                                        "placeId", placeId,
                                        "estimatedDuration", request.getEstimatedDuration() != null ? request.getEstimatedDuration() : 0,
                                        "estimatedCost", request.getEstimatedCost() != null ? request.getEstimatedCost() : 0,
                                        "memo", request.getMemo() != null ? request.getMemo() : "",
                                        "reservationUrl", request.getReservationUrl() != null ? request.getReservationUrl() : ""
                                )
                        )
                )
        );

        return PlaceDetailConverter.toDetail(place, placeDetail, voteQueryService.getVoteSummary(placeId, memberId));
    }

    @Transactional
    public PlaceDetailResponseDto.Detail updatePlaceFromWebSocket(EditSaveMessage msg) {
        Place place = placeRepository.findById(msg.getPlaceId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 장소입니다."));
        place.update(msg.getEstimatedDuration(), msg.getEstimatedCost(),
                msg.getMemo(), msg.getReservationUrl());
        PlaceDetail placeDetail = placeDetailRepository.findByPlaceId(msg.getPlaceId()).orElse(null);
        return PlaceDetailConverter.toDetail(place, placeDetail, voteQueryService.getVoteSummary(msg.getPlaceId(), msg.getMemberId()));
    }
}
