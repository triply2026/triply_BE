package com.example.triply.ai.service;

import com.example.triply.ai.client.GeminiApiClient;
import com.example.triply.ai.dto.PlaceDetailAiResponseDto;
import com.example.triply.ai.parser.ResponseParser;
import com.example.triply.ai.prompt.PromptBuilder;
import com.example.triply.tripPlan.entity.Place;
import com.example.triply.tripPlan.entity.PlaceDetail;
import com.example.triply.tripPlan.repository.PlaceDetailRepository;
import com.example.triply.tripPlan.repository.PlaceRepository;
import com.example.triply.websocket.dto.PlanEventMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlaceDetailGenerationService {

    private final GeminiApiClient geminiApiClient;
    private final PromptBuilder promptBuilder;
    private final ResponseParser responseParser;
    private final PlaceRepository placeRepository;
    private final PlaceDetailRepository placeDetailRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Async("placeDetailExecutor")
    @Transactional
    public void generateAsync(Place place, Long planId) {
        try {
            String prompt = promptBuilder.buildPlaceDetailPrompt(place.getName(), place.getAddress());
            String rawJson = geminiApiClient.generateContent(prompt);
            PlaceDetailAiResponseDto aiResponse = responseParser.parsePlaceDetail(rawJson);

            Place managedPlace = placeRepository.findById(place.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Place not found: " + place.getId()));
            if (aiResponse.getReservationUrl() != null) {
                managedPlace.updateReservationUrl(aiResponse.getReservationUrl());
            }

            placeDetailRepository.findByPlaceId(place.getId()).ifPresentOrElse(
                    detail -> detail.updateFromAi(aiResponse.getDescription(), aiResponse.getSourceUrls(), aiResponse.getImages()),
                    () -> placeDetailRepository.save(PlaceDetail.builder()
                            .place(managedPlace)
                            .description(aiResponse.getDescription())
                            .sourceUrls(aiResponse.getSourceUrls())
                            .image(aiResponse.getImages())
                            .build())
            );

            messagingTemplate.convertAndSend("/topic/plan/" + planId,
                    PlanEventMessage.of(
                            PlanEventMessage.EventType.PLACE_DETAIL_READY,
                            planId, null, null,
                            Map.of(
                                    "placeId", place.getId(),
                                    "description", aiResponse.getDescription() != null ? aiResponse.getDescription() : "",
                                    "sourceUrls", aiResponse.getSourceUrls() != null ? aiResponse.getSourceUrls() : java.util.List.of(),
                                    "images", aiResponse.getImages() != null ? aiResponse.getImages() : java.util.List.of(),
                                    "reservationUrl", aiResponse.getReservationUrl() != null ? aiResponse.getReservationUrl() : ""
                            )
                    )
            );

            log.info("Place detail generated: placeId={}, planId={}", place.getId(), planId);
        } catch (Exception e) {
            log.error("Failed to generate place detail: placeId={}, planId={}", place.getId(), planId, e);
        }
    }
}
