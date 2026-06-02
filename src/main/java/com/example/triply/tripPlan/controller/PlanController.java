package com.example.triply.tripPlan.controller;

import com.example.triply.tripPlan.entity.Plan;
import com.example.triply.tripPlan.entity.enums.PlanStatus;
import com.example.triply.tripPlan.service.PlanCommandService;
import com.example.triply.tripPlan.service.PlanQueryService;
import com.example.triply.tripPlan.service.PlanShareService;
import com.example.triply.websocket.service.ParticipantSessionService;
import com.example.triply.websocket.service.PlaceEditLockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    private final PlanCommandService planCommandService;
    private final PlanQueryService planQueryService;
    private final PlanShareService planShareService;
    private final ParticipantSessionService participantSessionService;
    private final PlaceEditLockService placeEditLockService;

    @GetMapping
    @Operation(summary = "내 플랜 목록 조회")
    public ResponseEntity<List<PlanQueryService.PlanSummaryDto>> getMyPlans(HttpSession session) {
        Long memberId = (Long) session.getAttribute("memberId");
        if (memberId == null) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }
        return ResponseEntity.ok(planQueryService.getMyPlans(memberId));
    }

    @GetMapping("/{planId}")
    @Operation(summary = "플랜 상세 조회 (초기 렌더링용)")
    public ResponseEntity<PlanQueryService.PlanStateDto> getPlanDetail(@PathVariable Long planId) {
        return ResponseEntity.ok(planQueryService.getPlanState(planId));
    }

    @PostMapping("/{planId}/share")
    @Operation(summary = "공유 링크 생성")
    public ResponseEntity<ShareLinkResponse> generateShareLink(@PathVariable Long planId) {
        PlanShareService.ShareResult result = planShareService.generateShareLink(planId);
        return ResponseEntity.ok(ShareLinkResponse.builder()
                .shareToken(result.getShareToken())
                .shareUrl(result.getShareUrl())
                .build());
    }

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

    @DeleteMapping("/{planId}")
    @Operation(summary = "플랜 삭제")
    public ResponseEntity<Void> deletePlan(@PathVariable Long planId) {
        planCommandService.deletePlan(planId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("/{planId}/confirm")
    @Operation(summary = "일정 확정")
    public ResponseEntity<ConfirmResponse> confirmPlan(@PathVariable Long planId) {
        PlanStatus status = planCommandService.confirmPlan(planId);
        return ResponseEntity.ok(ConfirmResponse.builder()
                .planId(planId)
                .status(status)
                .build());
    }

    @PatchMapping("/{planId}/unconfirm")
    @Operation(summary = "일정 확정 해제")
    public ResponseEntity<ConfirmResponse> unconfirmPlan(@PathVariable Long planId) {
        PlanStatus status = planCommandService.unconfirmPlan(planId);
        return ResponseEntity.ok(ConfirmResponse.builder()
                .planId(planId)
                .status(status)
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
                .status(state.getStatus())
                .days(state.getDays())
                .participants(participants)
                .editLocks(editLocks)
                .build());
    }

    @Getter
    @Builder
    public static class ShareLinkResponse {
        private String shareToken;
        private String shareUrl;
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
        private PlanStatus status;
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

    @Getter
    @Builder
    public static class ConfirmResponse {
        private Long planId;
        private PlanStatus status;
    }
}
