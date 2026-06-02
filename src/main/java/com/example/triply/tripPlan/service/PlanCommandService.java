package com.example.triply.tripPlan.service;

import com.example.triply.member.repository.PlanMemberRepository;
import com.example.triply.tripPlan.entity.Days;
import com.example.triply.tripPlan.entity.Place;
import com.example.triply.tripPlan.entity.Plan;
import com.example.triply.tripPlan.entity.enums.PlanStatus;
import com.example.triply.tripPlan.repository.DaysRepository;
import com.example.triply.tripPlan.repository.PlaceDetailRepository;
import com.example.triply.tripPlan.repository.PlaceRepository;
import com.example.triply.tripPlan.repository.PlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlanCommandService {

    private final PlanRepository planRepository;
    private final PlanMemberRepository planMemberRepository;
    private final DaysRepository daysRepository;
    private final PlaceRepository placeRepository;
    private final PlaceDetailRepository placeDetailRepository;

    @Transactional
    public void deletePlan(Long planId) {
        if (!planRepository.existsById(planId)) {
            throw new IllegalArgumentException("존재하지 않는 플랜입니다: " + planId);
        }

        List<Long> dayIds = daysRepository.findByPlanIdOrderByDayNumber(planId).stream()
                .map(Days::getId)
                .collect(Collectors.toList());

        if (!dayIds.isEmpty()) {
            List<Long> placeIds = placeRepository.findByDayIdIn(dayIds).stream()
                    .map(Place::getId)
                    .collect(Collectors.toList());

            if (!placeIds.isEmpty()) {
                placeDetailRepository.deleteByPlaceIdIn(placeIds);
            }
            placeRepository.deleteByDayIdIn(dayIds);
        }

        daysRepository.deleteByPlanId(planId);
        planMemberRepository.deleteByPlanId(planId);
        planRepository.deleteById(planId);
    }

    @Transactional
    public PlanStatus confirmPlan(Long planId) {
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 플랜입니다: " + planId));
        if (plan.isConfirmed()) {
            throw new IllegalStateException("이미 확정된 플랜입니다.");
        }
        plan.confirm();
        planRepository.save(plan);
        return plan.getStatus();
    }

    @Transactional
    public PlanStatus unconfirmPlan(Long planId) {
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 플랜입니다: " + planId));
        if (!plan.isConfirmed()) {
            throw new IllegalStateException("확정되지 않은 플랜입니다.");
        }
        plan.unconfirm();
        planRepository.save(plan);
        return plan.getStatus();
    }
}
