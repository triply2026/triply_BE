package com.example.triply.ai.parser;

import com.example.triply.ai.dto.ItineraryResponseDto;
import com.example.triply.ai.exception.GeminiApiException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ResponseParser {

    private final ObjectMapper objectMapper;

    public ItineraryResponseDto parseItinerary(String rawJson) {
        try {
            String cleaned = stripMarkdownFence(rawJson);
            return objectMapper.readValue(cleaned, ItineraryResponseDto.class);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse Gemini response. raw={}", rawJson, e);
            throw new GeminiApiException("Gemini 응답 파싱 실패: " + e.getOriginalMessage(), e);
        }
    }

    // Gemini가 ```json ... ``` 블록으로 감싸는 경우 제거
    private String stripMarkdownFence(String raw) {
        String trimmed = raw.strip();
        if (trimmed.startsWith("```json")) {
            trimmed = trimmed.substring(7);
        } else if (trimmed.startsWith("```")) {
            trimmed = trimmed.substring(3);
        }
        if (trimmed.endsWith("```")) {
            trimmed = trimmed.substring(0, trimmed.length() - 3);
        }
        return trimmed.strip();
    }
}
