package com.example.triply.websocket.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ConfirmPlanMessage {
    private Long memberId;
    private String nickname;
}
