package com.example.triply.ai.service;

import com.example.triply.ai.client.GeminiApiClient;
import com.example.triply.ai.dto.ItineraryRequestDto;
import com.example.triply.ai.dto.ItineraryResponseDto;
import com.example.triply.ai.parser.ResponseParser;
import com.example.triply.ai.prompt.PromptBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItineraryGenerationService {

    private final GeminiApiClient geminiApiClient;
    private final PromptBuilder promptBuilder;
    private final ResponseParser responseParser;

    public ItineraryResponseDto generateItinerary(ItineraryRequestDto request) {
        log.info("Generating itinerary: destination={}, {} ~ {}",
                request.getDestination(), request.getStartDate(), request.getEndDate());

        String prompt = promptBuilder.buildItineraryPrompt(request);
        String rawResponse = geminiApiClient.generateContent(prompt);
        return responseParser.parseItinerary(rawResponse);
    }


}
