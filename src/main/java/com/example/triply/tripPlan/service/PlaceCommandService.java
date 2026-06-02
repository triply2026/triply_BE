package com.example.triply.tripPlan.service;

import com.example.triply.ai.service.PlaceDetailGenerationService;
import com.example.triply.auth.repository.AuthRepository;
import com.example.triply.member.entity.Member;
import com.example.triply.tripPlan.entity.Days;
import com.example.triply.tripPlan.entity.Place;
import com.example.triply.tripPlan.repository.DaysRepository;
import com.example.triply.tripPlan.repository.PlaceDetailRepository;
import com.example.triply.tripPlan.repository.PlaceRepository;
import com.example.triply.websocket.dto.AddPlaceMessage;
import com.example.triply.websocket.dto.ReorderMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlaceCommandService {

    private final PlaceRepository placeRepository;
    private final PlaceDetailRepository placeDetailRepository;
    private final DaysRepository daysRepository;
    private final AuthRepository authRepository;
    private final PlaceDetailGenerationService placeDetailGenerationService;

    @Transactional
    public void reorderPlaces(ReorderMessage msg) {
        for (ReorderMessage.PlaceOrder order : msg.getPlaceOrders()) {
            placeRepository.updateOrderIndex(order.getPlaceId(), order.getOrderIndex());
        }
    }

    @Transactional
    public Place addPlace(AddPlaceMessage msg, Long planId) {
        Days day = daysRepository.findById(msg.getDayId())
                .orElseThrow(() -> new IllegalArgumentException("Day not found: " + msg.getDayId()));
        Member member = authRepository.findById(msg.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("Member not found: " + msg.getMemberId()));

        int nextOrder = placeRepository.findByDayIdOrderByOrderIndex(msg.getDayId()).size();
        Place place = new Place(
                null,
                day,
                member,
                msg.getName(),
                msg.getAddress(),
                msg.getEstimatedCost(),
                msg.getStayDurationMin(),
                msg.getLatitude(),
                msg.getLongitude(),
                msg.getMapPlaceId(),
                msg.getCategory(),
                nextOrder,
                msg.getMemo(),
                null
        );
        Place saved = placeRepository.save(place);
        placeDetailGenerationService.generateAsync(saved, planId);
        return saved;
    }

    @Transactional
    public void deletePlace(Long placeId) {
        placeDetailRepository.deleteByPlaceId(placeId);
        placeRepository.deleteById(placeId);
    }
}
