package com.example.triply.websocket.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PlanEventMessage {

    private EventType type;
    private Long planId;
    private Long memberId;
    private String nickname;
    private Object payload;
    private LocalDateTime timestamp;

    public enum EventType {
        PARTICIPANTS_UPDATED,
        PLACE_ORDER_CHANGED,
        PLACE_EDIT_STARTED,
        PLACE_EDIT_ENDED,
        PLACE_ADDED,
        PLACE_DELETED,
        PLACE_UPDATED,
        PLACE_DETAIL_READY,
        PLAN_CONFIRMED,
        PLAN_UNCONFIRMED
    }

    public static PlanEventMessage of(EventType type, Long planId, Long memberId, String nickname, Object payload) {
        return PlanEventMessage.builder()
                .type(type)
                .planId(planId)
                .memberId(memberId)
                .nickname(nickname)
                .payload(payload)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
