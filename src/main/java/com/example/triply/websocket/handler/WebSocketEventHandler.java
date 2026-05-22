package com.example.triply.websocket.handler;

import com.example.triply.websocket.dto.PlanEventMessage;
import com.example.triply.websocket.service.ParticipantSessionService;
import com.example.triply.websocket.service.PlaceEditLockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketEventHandler {

    private final ParticipantSessionService participantSessionService;
    private final PlaceEditLockService placeEditLockService;
    private final SimpMessagingTemplate messagingTemplate;

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        String sessionId = event.getSessionId();
        log.info("WebSocket disconnected: sessionId={}", sessionId);

        participantSessionService.getMemberIdBySessionId(sessionId).ifPresent(memberId -> {
            // 편집 잠금 전부 해제 후 이벤트 발송
            List<Long> releasedPlaceIds = placeEditLockService.releaseAllByMember(memberId);
            participantSessionService.removeBySessionId(sessionId).ifPresent(planId -> {
                // 잠금 해제 이벤트 브로드캐스트
                releasedPlaceIds.forEach(placeId ->
                        messagingTemplate.convertAndSend(
                                "/topic/plan/" + planId,
                                PlanEventMessage.of(
                                        PlanEventMessage.EventType.PLACE_EDIT_ENDED,
                                        planId, memberId, null,
                                        Map.of("placeId", placeId)
                                )
                        )
                );

                // 참여자 목록 업데이트 브로드캐스트
                messagingTemplate.convertAndSend(
                        "/topic/plan/" + planId,
                        PlanEventMessage.of(
                                PlanEventMessage.EventType.PARTICIPANTS_UPDATED,
                                planId, memberId, null,
                                Map.of("participants", participantSessionService.getParticipants(planId))
                        )
                );
            });
        });
    }
}
