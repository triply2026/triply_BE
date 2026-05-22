package com.example.triply.websocket.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ReorderMessage {
    private Long memberId;
    private String nickname;
    private Long dayId;
    private List<PlaceOrder> placeOrders;

    @Getter
    @NoArgsConstructor
    public static class PlaceOrder {
        private Long placeId;
        private Integer orderIndex;
    }
}
