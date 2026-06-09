package com.example.triply.tripPlan.service;

import com.example.triply.tripPlan.converter.PlaceDetailConverter;
import com.example.triply.tripPlan.dto.PlaceDetailResponseDto;
import com.example.triply.tripPlan.entity.Place;
import com.example.triply.tripPlan.entity.PlaceDetail;
import com.example.triply.tripPlan.repository.PlaceDetailRepository;
import com.example.triply.tripPlan.repository.PlaceRepository;
import com.example.triply.vote.dto.VoteResponseDto;
import com.example.triply.vote.service.VoteQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlaceDetailQueryService {

    private final PlaceRepository placeRepository;
    private final PlaceDetailRepository placeDetailRepository;
    private final VoteQueryService voteQueryService;

    public PlaceDetailResponseDto.Detail getPlaceDetail(Long placeId, Long memberId) {
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 장소입니다."));
        PlaceDetail placeDetail = placeDetailRepository.findByPlaceId(placeId).orElse(null);
        VoteResponseDto.VoteSummary voteSummary = voteQueryService.getVoteSummary(placeId, memberId);
        return PlaceDetailConverter.toDetail(place, placeDetail, voteSummary);
    }
}
