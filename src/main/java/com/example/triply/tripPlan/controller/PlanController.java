package com.example.triply.tripPlan.controller;

import com.example.triply.tripPlan.entity.Plan;
import com.example.triply.tripPlan.service.PlanQueryService;
import com.example.triply.websocket.service.ParticipantSessionService;
import com.example.triply.websocket.service.PlaceEditLockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@Tag(name = "여행 계획")
@RequestMapping("/api/v1/plans")
@RequiredArgsConstructor
public class PlanController {

    private final PlanQueryService planQueryService;
    private final ParticipantSessionService participantSessionService;
    private final PlaceEditLockService placeEditLockService;

    @GetMapping("/shared/{token}")
    @Operation(summary = "공유 링크로 플랜 기본 정보 조회")
    public ResponseEntity<SharedPlanResponse> getBySharedToken(@PathVariable String token) {
        Plan plan = planQueryService.findBySharedToken(token);
        return ResponseEntity.ok(SharedPlanResponse.builder()
                .planId(plan.getId())
                .title(plan.getTitle())
                .destination(plan.getDestination())
                .startDate(plan.getStartDate().toString())
                .endDate(plan.getEndDate().toString())
                .build());
    }

    @GetMapping("/{planId}/state")
    @Operation(summary = "플랜 전체 현재 상태 조회 (재연결 동기화용)")
    public ResponseEntity<PlanStateResponse> getPlanState(@PathVariable Long planId) {
        PlanQueryService.PlanStateDto state = planQueryService.getPlanState(planId);

        List<Long> allPlaceIds = state.getDays().stream()
                .flatMap(d -> d.getPlaces().stream())
                .map(PlanQueryService.PlaceStateDto::getPlaceId)
                .collect(Collectors.toList());

        Map<Long, PlaceEditLockService.EditLockInfo> lockMap = placeEditLockService.getLocksForPlaces(allPlaceIds);

        List<EditLockDto> editLocks = lockMap.entrySet().stream()
                .map(e -> EditLockDto.builder()
                        .placeId(e.getKey())
                        .memberId(e.getValue().getMemberId())
                        .nickname(e.getValue().getNickname())
                        .build())
                .collect(Collectors.toList());

        List<ParticipantDto> participants = participantSessionService.getParticipants(planId).stream()
                .map(p -> ParticipantDto.builder()
                        .memberId(p.getMemberId())
                        .nickname(p.getNickname())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(PlanStateResponse.builder()
                .planId(state.getPlanId())
                .title(state.getTitle())
                .destination(state.getDestination())
                .days(state.getDays())
                .participants(participants)
                .editLocks(editLocks)
                .build());
    }

    @Getter
    @Builder
    public static class SharedPlanResponse {
        private Long planId;
        private String title;
        private String destination;
        private String startDate;
        private String endDate;
    }

    @Getter
    @Builder
    public static class PlanStateResponse {
        private Long planId;
        private String title;
        private String destination;
        private List<PlanQueryService.DayStateDto> days;
        private List<ParticipantDto> participants;
        private List<EditLockDto> editLocks;
    }

    @Getter
    @Builder
    public static class ParticipantDto {
        private Long memberId;
        private String nickname;
    }

    @Getter
    @Builder
    public static class EditLockDto {
        private Long placeId;
        private Long memberId;
        private String nickname;
    }
}
