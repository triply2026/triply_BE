package com.example.triply.websocket.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EditSaveMessage {
    private Long memberId;
    private String nickname;
    private Long placeId;
    private Integer estimatedDuration;
    private Integer estimatedCost;
    private String memo;
    private String reservationUrl;
}
