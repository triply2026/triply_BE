package com.example.triply.vote.service;

import com.example.triply.vote.converter.VoteConverter;
import com.example.triply.vote.dto.VoteResponseDto;
import com.example.triply.vote.entity.Vote;
import com.example.triply.vote.entity.enums.VoteType;
import com.example.triply.vote.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VoteQueryService {

    private final VoteRepository voteRepository;

    /**
     * 특정 장소의 투표 집계를 조회한다.
     *
     * @param placeId  장소 ID
     * @param memberId 현재 사용자 ID (본인 투표 표시용, nullable)
     * @return 투표 집계 정보
     */
    public VoteResponseDto.VoteSummary getVoteSummary(Long placeId, Long memberId) {
        long likeCount = voteRepository.countByPlaceIdAndVoteType(placeId, VoteType.LIKE);
        long dislikeCount = voteRepository.countByPlaceIdAndVoteType(placeId, VoteType.DISLIKE);

        VoteType myVoteType = null;
        if (memberId != null) {
            myVoteType = voteRepository.findByMemberIdAndPlaceId(memberId, placeId)
                    .map(Vote::getVoteType)
                    .orElse(null);
        }

        return VoteConverter.toVoteSummary(placeId, likeCount, dislikeCount, myVoteType);
    }

    /**
     * 여러 장소의 투표 집계를 일괄 조회한다. (N+1 방지)
     *
     * @param placeIds 장소 ID 목록
     * @param memberId 현재 사용자 ID (nullable)
     * @return 장소별 투표 집계 목록
     */
    public List<VoteResponseDto.VoteSummary> getVoteSummaries(List<Long> placeIds, Long memberId) {
        if (placeIds == null || placeIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<Object[]> counts = voteRepository.countByPlaceIdInGroupByVoteType(placeIds);
        Map<Long, Map<VoteType, Long>> countMap = new HashMap<>();
        for (Object[] row : counts) {
            Long placeId = (Long) row[0];
            VoteType type = (VoteType) row[1];
            Long count = (Long) row[2];
            countMap.computeIfAbsent(placeId, k -> new EnumMap<>(VoteType.class))
                    .put(type, count);
        }

        Map<Long, VoteType> myVoteMap = new HashMap<>();
        if (memberId != null) {
            List<Vote> myVotes = voteRepository.findAllByPlaceIdIn(placeIds).stream()
                    .filter(v -> v.getMember().getId().equals(memberId))
                    .collect(Collectors.toList());
            for (Vote v : myVotes) {
                myVoteMap.put(v.getPlace().getId(), v.getVoteType());
            }
        }

        return placeIds.stream()
                .map(placeId -> {
                    Map<VoteType, Long> typeCount = countMap.getOrDefault(placeId, Collections.emptyMap());
                    long likeCount = typeCount.getOrDefault(VoteType.LIKE, 0L);
                    long dislikeCount = typeCount.getOrDefault(VoteType.DISLIKE, 0L);
                    VoteType myVoteType = myVoteMap.get(placeId);
                    return VoteConverter.toVoteSummary(placeId, likeCount, dislikeCount, myVoteType);
                })
                .collect(Collectors.toList());
    }

    /**
     * 특정 Day의 장소를 좋아요(LIKE) 수 기준 내림차순으로 정렬된 장소 ID 목록을 반환한다.
     *
     * @param dayId Day ID
     * @return 좋아요 수 기준 내림차순 장소 ID 목록
     */
    public List<Long> getPlaceIdsSortedByLikes(Long dayId) {
        return voteRepository.findPlaceIdsByDayIdOrderByLikeCountDesc(dayId);
    }
}
