package com.example.triply.tripPlan.service;

import com.example.triply.tripPlan.converter.PlaceDetailConverter;
import com.example.triply.tripPlan.dto.PlaceDetailRequestDto;
import com.example.triply.tripPlan.dto.PlaceDetailResponseDto;
import com.example.triply.tripPlan.entity.Place;
import com.example.triply.tripPlan.entity.PlaceDetail;
import com.example.triply.tripPlan.repository.PlaceDetailRepository;
import com.example.triply.tripPlan.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlaceDetailCommandService {

    private final PlaceRepository placeRepository;
    private final PlaceDetailRepository placeDetailRepository;

    @Transactional
    public PlaceDetailResponseDto.Detail updatePlace(Long placeId, PlaceDetailRequestDto.Update request) {
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 장소입니다."));
        place.update(request.getEstimatedDuration(), request.getEstimatedCost(),
                request.getMemo(), request.getReservationUrl());
        PlaceDetail placeDetail = placeDetailRepository.findByPlaceId(placeId).orElse(null);
        return PlaceDetailConverter.toDetail(place, placeDetail);
    }
}
