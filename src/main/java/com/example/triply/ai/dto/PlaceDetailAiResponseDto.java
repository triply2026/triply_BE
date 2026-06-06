package com.example.triply.ai.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlaceDetailAiResponseDto {
    private String description;
    private List<String> sourceUrls;
    private List<String> images;
    private String reservationUrl;
}
