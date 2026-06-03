package com.example.triply.tripPlan.service;

import com.example.triply.member.entity.PlanMember;
import com.example.triply.member.repository.PlanMemberRepository;
import com.example.triply.tripPlan.entity.Days;
import com.example.triply.tripPlan.entity.Place;
import com.example.triply.tripPlan.entity.Plan;
import com.example.triply.tripPlan.entity.enums.PlanStatus;
import com.example.triply.tripPlan.repository.DaysRepository;
import com.example.triply.tripPlan.repository.PlaceRepository;
import com.example.triply.tripPlan.repository.PlanRepository;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlanQueryService {

    private final PlanRepository planRepository;
    private final DaysRepository daysRepository;
    private final PlaceRepository placeRepository;
    private final PlanMemberRepository planMemberRepository;

    @Transactional(readOnly = true)
    public List<PlanSummaryDto> getMyPlans(Long memberId) {
        return planMemberRepository.findByMemberId(memberId).stream()
                .map(PlanMember::getPlan)
                .map(plan -> PlanSummaryDto.builder()
                        .planId(plan.getId())
                        .title(plan.getTitle())
                        .destination(plan.getDestination())
                        .startDate(plan.getStartDate())
                        .endDate(plan.getEndDate())
                        .status(plan.getStatus())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Plan findBySharedToken(String token) {
        return planRepository.findBySharedToken(token)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 공유 링크입니다."));
    }

    @Transactional(readOnly = true)
    public PlanStateDto getPlanState(Long planId) {
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("Plan not found: " + planId));

        List<Days> days = daysRepository.findByPlanIdOrderByDayNumber(planId);
        List<Long> dayIds = days.stream().map(Days::getId).toList();
        List<Place> places = placeRepository.findByDayIdIn(dayIds);

        List<DayStateDto> dayStates = days.stream().map(day -> {
            List<PlaceStateDto> placeDtos = places.stream()
                    .filter(p -> p.getDay().getId().equals(day.getId()))
                    .sorted((a, b) -> {
                        int ai = a.getOrderIndex() == null ? 0 : a.getOrderIndex();
                        int bi = b.getOrderIndex() == null ? 0 : b.getOrderIndex();
                        return Integer.compare(ai, bi);
                    })
                    .map(p -> PlaceStateDto.builder()
                            .placeId(p.getId())
                            .name(p.getName())
                            .address(p.getAddress())
                            .category(p.getCategory() != null ? p.getCategory().name() : null)
                            .estimatedCost(p.getEstimatedCost())
                            .stayDurationMin(p.getStayDurationMin())
                            .latitude(p.getLatitude())
                            .longitude(p.getLongitude())
                            .orderIndex(p.getOrderIndex())
                            .build())
                    .collect(Collectors.toList());

            return DayStateDto.builder()
                    .dayId(day.getId())
                    .dayNumber(day.getDayNumber())
                    .date(day.getDate())
                    .places(placeDtos)
                    .build();
        }).collect(Collectors.toList());

        return PlanStateDto.builder()
                .planId(plan.getId())
                .title(plan.getTitle())
                .destination(plan.getDestination())
                .status(plan.getStatus())
                .days(dayStates)
                .build();
    }

    @Getter
    @Builder
    public static class PlanSummaryDto {
        private Long planId;
        private String title;
        private String destination;
        private LocalDate startDate;
        private LocalDate endDate;
        private PlanStatus status;
    }

    @Getter
    @Builder
    public static class PlanStateDto {
        private Long planId;
        private String title;
        private String destination;
        private PlanStatus status;
        private List<DayStateDto> days;
    }

    @Getter
    @Builder
    public static class DayStateDto {
        private Long dayId;
        private Integer dayNumber;
        private LocalDate date;
        private List<PlaceStateDto> places;
    }

    @Getter
    @Builder
    public static class PlaceStateDto {
        private Long placeId;
        private String name;
        private String address;
        private String category;
        private Integer estimatedCost;
        private Integer stayDurationMin;
        private Double latitude;
        private Double longitude;
        private Integer orderIndex;
    }
}
