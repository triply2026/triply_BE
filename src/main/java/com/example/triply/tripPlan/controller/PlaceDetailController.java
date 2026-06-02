package com.example.triply.tripPlan.controller;

import com.example.triply.tripPlan.dto.PlaceDetailRequestDto;
import com.example.triply.tripPlan.dto.PlaceDetailResponseDto;
import com.example.triply.tripPlan.service.PlaceDetailCommandService;
import com.example.triply.tripPlan.service.PlaceDetailQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "장소 상세")
@RequestMapping("/api/v1/places")
@RequiredArgsConstructor
public class PlaceDetailController {

    private final PlaceDetailQueryService placeDetailQueryService;
    private final PlaceDetailCommandService placeDetailCommandService;

    @GetMapping("/{placeId}")
    @Operation(summary = "장소 상세 조회")
    public ResponseEntity<PlaceDetailResponseDto.Detail> getPlaceDetail(
            @PathVariable Long placeId,
            HttpSession session) {
        Long memberId = (Long) session.getAttribute("memberId");
        return ResponseEntity.ok(placeDetailQueryService.getPlaceDetail(placeId, memberId));
    }

    @PutMapping("/{placeId}")
    @Operation(summary = "장소 상세 편집 저장")
    public ResponseEntity<PlaceDetailResponseDto.Detail> updatePlace(
            @PathVariable Long placeId,
            @RequestBody PlaceDetailRequestDto.Update request) {
        return ResponseEntity.ok(placeDetailCommandService.updatePlace(placeId, request));
    }
}
