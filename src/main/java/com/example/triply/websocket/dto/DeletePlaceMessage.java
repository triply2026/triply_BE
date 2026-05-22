package com.example.triply.websocket.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DeletePlaceMessage {
    private Long memberId;
    private String nickname;
    private Long placeId;
}
