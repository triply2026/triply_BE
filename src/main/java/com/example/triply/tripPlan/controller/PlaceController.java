package com.example.triply.tripPlan.controller;

import com.example.triply.tripPlan.service.PlaceCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "장소")
@RequestMapping("/api/v1/places")
@RequiredArgsConstructor
public class PlaceController {

    private final PlaceCommandService placeCommandService;

    @DeleteMapping("/{placeId}")
    @Operation(summary = "장소 삭제")
    public ResponseEntity<Void> deletePlace(@PathVariable Long placeId) {
        placeCommandService.deletePlace(placeId);
        return ResponseEntity.noContent().build();
    }
}
