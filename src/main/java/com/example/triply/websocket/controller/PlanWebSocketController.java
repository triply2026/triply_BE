package com.example.triply.websocket.controller;

import com.example.triply.tripPlan.entity.Place;
import com.example.triply.tripPlan.service.PlaceCommandService;
import com.example.triply.tripPlan.service.PlaceDetailCommandService;
import com.example.triply.websocket.dto.*;
import com.example.triply.websocket.service.ParticipantSessionService;
import com.example.triply.websocket.service.PlaceEditLockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class PlanWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ParticipantSessionService participantSessionService;
    private final PlaceEditLockService placeEditLockService;
    private final PlaceCommandService placeCommandService;
    private final PlaceDetailCommandService placeDetailCommandService;

    @MessageMapping("/plan/{planId}/join")
    public void join(@DestinationVariable Long planId,
                     @Payload JoinMessage msg,
                     SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        participantSessionService.addParticipant(planId, sessionId, msg.getMemberId(), msg.getNickname());
        log.info("join: planId={}, memberId={}, sessionId={}", planId, msg.getMemberId(), sessionId);

        broadcast(planId, PlanEventMessage.of(
                PlanEventMessage.EventType.PARTICIPANTS_UPDATED,
                planId, msg.getMemberId(), msg.getNickname(),
                Map.of("participants", participantSessionService.getParticipants(planId))
        ));
    }

    @MessageMapping("/plan/{planId}/place/reorder")
    public void reorderPlace(@DestinationVariable Long planId,
                             @Payload ReorderMessage msg) {
        placeCommandService.reorderPlaces(msg);
        log.info("reorder: planId={}, dayId={}, memberId={}", planId, msg.getDayId(), msg.getMemberId());

        broadcast(planId, PlanEventMessage.of(
                PlanEventMessage.EventType.PLACE_ORDER_CHANGED,
                planId, msg.getMemberId(), msg.getNickname(),
                Map.of("dayId", msg.getDayId(), "placeOrders", msg.getPlaceOrders())
        ));
    }

    @MessageMapping("/plan/{planId}/place/edit/start")
    public void startEditing(@DestinationVariable Long planId,
                             @Payload EditStartMessage msg,
                             SimpMessageHeaderAccessor headerAccessor) {
        boolean acquired = placeEditLockService.tryAcquire(msg.getPlaceId(), msg.getMemberId(), msg.getNickname());
        if (!acquired) {
            placeEditLockService.getLock(msg.getPlaceId()).ifPresent(lock ->
                    messagingTemplate.convertAndSendToUser(
                            headerAccessor.getSessionId(),
                            "/queue/error",
                            Map.of("message", "이미 편집 중입니다.", "lockedBy", lock.getNickname())
                    )
            );
            return;
        }
        log.info("edit start: planId={}, placeId={}, memberId={}", planId, msg.getPlaceId(), msg.getMemberId());

        broadcast(planId, PlanEventMessage.of(
                PlanEventMessage.EventType.PLACE_EDIT_STARTED,
                planId, msg.getMemberId(), msg.getNickname(),
                Map.of("placeId", msg.getPlaceId())
        ));
    }

    @MessageMapping("/plan/{planId}/place/edit/end")
    public void endEditing(@DestinationVariable Long planId,
                           @Payload EditEndMessage msg) {
        boolean released = placeEditLockService.release(msg.getPlaceId(), msg.getMemberId());
        if (!released) return;
        log.info("edit end: planId={}, placeId={}, memberId={}", planId, msg.getPlaceId(), msg.getMemberId());

        broadcast(planId, PlanEventMessage.of(
                PlanEventMessage.EventType.PLACE_EDIT_ENDED,
                planId, msg.getMemberId(), msg.getNickname(),
                Map.of("placeId", msg.getPlaceId())
        ));
    }

    @MessageMapping("/plan/{planId}/place/add")
    public void addPlace(@DestinationVariable Long planId,
                         @Payload AddPlaceMessage msg) {
        Place saved = placeCommandService.addPlace(msg, planId);
        log.info("place added: planId={}, placeId={}, memberId={}", planId, saved.getId(), msg.getMemberId());

        broadcast(planId, PlanEventMessage.of(
                PlanEventMessage.EventType.PLACE_ADDED,
                planId, msg.getMemberId(), msg.getNickname(),
                Map.of(
                        "placeId", saved.getId(),
                        "dayId", msg.getDayId(),
                        "name", msg.getName(),
                        "address", msg.getAddress() != null ? msg.getAddress() : "",
                        "category", msg.getCategory() != null ? msg.getCategory().name() : "",
                        "orderIndex", saved.getOrderIndex() != null ? saved.getOrderIndex() : 0,
                        "memo", msg.getMemo() != null ? msg.getMemo() : ""
                )
        ));
    }

    @MessageMapping("/plan/{planId}/place/edit/save")
    public void saveEditing(@DestinationVariable Long planId,
                            @Payload EditSaveMessage msg) {
        placeDetailCommandService.updatePlaceFromWebSocket(msg);
        log.info("place updated: planId={}, placeId={}, memberId={}", planId, msg.getPlaceId(), msg.getMemberId());

        broadcast(planId, PlanEventMessage.of(
                PlanEventMessage.EventType.PLACE_UPDATED,
                planId, msg.getMemberId(), msg.getNickname(),
                Map.of(
                        "placeId", msg.getPlaceId(),
                        "estimatedDuration", msg.getEstimatedDuration() != null ? msg.getEstimatedDuration() : 0,
                        "estimatedCost", msg.getEstimatedCost() != null ? msg.getEstimatedCost() : 0,
                        "memo", msg.getMemo() != null ? msg.getMemo() : "",
                        "reservationUrl", msg.getReservationUrl() != null ? msg.getReservationUrl() : ""
                )
        ));
    }

    @MessageMapping("/plan/{planId}/place/delete")
    public void deletePlace(@DestinationVariable Long planId,
                            @Payload DeletePlaceMessage msg) {
        placeEditLockService.release(msg.getPlaceId(), msg.getMemberId());
        placeCommandService.deletePlace(msg.getPlaceId());
        log.info("place deleted: planId={}, placeId={}, memberId={}", planId, msg.getPlaceId(), msg.getMemberId());

        broadcast(planId, PlanEventMessage.of(
                PlanEventMessage.EventType.PLACE_DELETED,
                planId, msg.getMemberId(), msg.getNickname(),
                Map.of("placeId", msg.getPlaceId())
        ));
    }

    private void broadcast(Long planId, PlanEventMessage message) {
        messagingTemplate.convertAndSend("/topic/plan/" + planId, message);
    }
}
