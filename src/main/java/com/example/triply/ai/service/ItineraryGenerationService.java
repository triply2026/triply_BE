package com.example.triply.ai.service;

import com.example.triply.ai.client.GeminiApiClient;
import com.example.triply.ai.dto.ItineraryRequestDto;
import com.example.triply.ai.dto.ItineraryResponseDto;
import com.example.triply.ai.parser.ResponseParser;
import com.example.triply.ai.prompt.PromptBuilder;
import com.example.triply.auth.repository.AuthRepository;
import com.example.triply.member.entity.Member;
import com.example.triply.member.entity.PlanMember;
import com.example.triply.member.entity.enums.PlanMemberRole;
import com.example.triply.member.repository.PlanMemberRepository;
import com.example.triply.tripPlan.entity.Days;
import com.example.triply.tripPlan.entity.Place;
import com.example.triply.tripPlan.entity.Plan;
import com.example.triply.tripPlan.entity.enums.PlaceCategory;
import com.example.triply.tripPlan.entity.enums.PlanStatus;
import com.example.triply.tripPlan.repository.DaysRepository;
import com.example.triply.tripPlan.repository.PlaceRepository;
import com.example.triply.tripPlan.repository.PlanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItineraryGenerationService {

    private final GeminiApiClient geminiApiClient;
    private final PromptBuilder promptBuilder;
    private final ResponseParser responseParser;
    private final AuthRepository authRepository;
    private final PlanRepository planRepository;
    private final DaysRepository daysRepository;
    private final PlaceRepository placeRepository;
    private final PlanMemberRepository planMemberRepository;

    @Transactional
    public ItineraryResponseDto generateItinerary(ItineraryRequestDto request, Long memberId) {
        log.info("Generating itinerary: destination={}, {} ~ {}",
                request.getDestination(), request.getStartDate(), request.getEndDate());

        Member member = authRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        String prompt = promptBuilder.buildItineraryPrompt(request);
        String rawResponse = geminiApiClient.generateContent(prompt);
        ItineraryResponseDto parsed = responseParser.parseItinerary(rawResponse);

        Plan plan = planRepository.save(Plan.builder()
                .title(request.getDestination() + " 여행")
                .destination(request.getDestination())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .memberCount(request.getMemberCount())
                .budget(request.getBudget())
                .tripStyle(request.getTripStyle())
                .status(PlanStatus.DRAFT)
                .build());

        planMemberRepository.save(PlanMember.builder()
                .member(member)
                .plan(plan)
                .role(PlanMemberRole.OWNER)
                .build());

        List<ItineraryResponseDto.DayPlan> days = parsed.getDays();
        if (days != null) {
            for (ItineraryResponseDto.DayPlan dayPlan : days) {
                Days savedDay = daysRepository.save(Days.builder()
                        .plan(plan)
                        .dayNumber(dayPlan.getDayNumber())
                        .date(dayPlan.getDate())
                        .build());

                List<ItineraryResponseDto.PlacePlan> places = dayPlan.getPlaces();
                if (places != null) {
                    for (int i = 0; i < places.size(); i++) {
                        ItineraryResponseDto.PlacePlan placePlan = places.get(i);
                        placeRepository.save(Place.builder()
                                .day(savedDay)
                                .member(member)
                                .name(placePlan.getName())
                                .address(placePlan.getAddress())
                                .estimatedCost(placePlan.getEstimatedCost())
                                .stayDurationMin(placePlan.getStayDurationMin())
                                .category(parseCategory(placePlan.getCategory()))
                                .orderIndex(i)
                                .build());
                    }
                }
            }
        }

        return ItineraryResponseDto.builder()
                .planId(plan.getId())
                .days(days)
                .build();
    }

    private PlaceCategory parseCategory(String category) {
        if (category == null) return PlaceCategory.ETC;
        try {
            return PlaceCategory.valueOf(category.toUpperCase());
        } catch (IllegalArgumentException e) {
            return PlaceCategory.ETC;
        }
    }


}
