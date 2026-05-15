package com.example.triply.ai.controller;

import com.example.triply.ai.dto.ItineraryRequestDto;
import com.example.triply.ai.dto.ItineraryResponseDto;
import com.example.triply.ai.service.ItineraryGenerationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
public class ItineraryGenerationController {

    private final ItineraryGenerationService itineraryGenerationService;

    @PostMapping("/itinerary/generate")
    public ResponseEntity<ItineraryResponseDto> generateItinerary(
            @RequestBody @Valid ItineraryRequestDto request) {
        return ResponseEntity.ok(itineraryGenerationService.generateItinerary(request));
    }
}
