package com.example.triply.ai.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class PlaceDetailAiResponseDto {
    private String description;
    private List<String> sourceUrls;
    private List<String> images;
    private String reservationUrl;
}
