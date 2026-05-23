package com.example.triply.tripPlan.service;

import com.example.triply.tripPlan.entity.Plan;
import com.example.triply.tripPlan.repository.PlanRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PlanShareService {

    private final PlanRepository planRepository;

    @Value("${app.share-base-url}")
    private String shareBaseUrl;

    @Transactional
    public ShareResult generateShareLink(Long planId) {
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("플랜을 찾을 수 없습니다."));

        String token = plan.getSharedToken();
        if (token == null) {
            token = UUID.randomUUID().toString();
            plan.assignSharedToken(token);
        }

        String shareUrl = shareBaseUrl + "/plans/shared/" + token;
        return new ShareResult(token, shareUrl);
    }

    @Getter
    @AllArgsConstructor
    public static class ShareResult {
        private String shareToken;
        private String shareUrl;
    }
}
