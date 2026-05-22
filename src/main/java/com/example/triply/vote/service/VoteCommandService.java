package com.example.triply.vote.service;

import com.example.triply.auth.repository.AuthRepository;
import com.example.triply.member.entity.Member;
import com.example.triply.tripPlan.entity.Place;
import com.example.triply.vote.converter.VoteConverter;
import com.example.triply.vote.dto.VoteResponseDto;
import com.example.triply.vote.entity.Vote;
import com.example.triply.vote.entity.enums.VoteType;
import com.example.triply.vote.repository.VoteRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VoteCommandService {

    private final VoteRepository voteRepository;
    private final AuthRepository authRepository;
    private final EntityManager entityManager;

    /**
     * 투표를 생성하거나 변경한다.
     * - 기존 투표가 없으면 새로 생성
     * - 같은 유형의 투표가 이미 있으면 투표 취소 (삭제)
     * - 다른 유형의 투표가 있으면 유형 변경
     *
     * @param memberId 투표하는 회원 ID
     * @param placeId  대상 장소 ID
     * @param voteType 투표 유형 (LIKE / DISLIKE)
     * @return 투표 정보 (취소 시 null)
     */
    @Transactional
    public VoteResponseDto.VoteInfo vote(Long memberId, Long placeId, VoteType voteType) {
        Member member = authRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        Place place = entityManager.find(Place.class, placeId);
        if (place == null) {
            throw new IllegalArgumentException("존재하지 않는 장소입니다.");
        }

        Optional<Vote> existing = voteRepository.findByMemberIdAndPlaceId(memberId, placeId);

        if (existing.isPresent()) {
            Vote vote = existing.get();
            if (vote.getVoteType() == voteType) {
                // 같은 유형 재클릭 → 투표 취소
                voteRepository.delete(vote);
                return null;
            } else {
                // 다른 유형 클릭 → 변경
                vote.changeVoteType(voteType);
                return VoteConverter.toVoteInfo(vote);
            }
        }

        // 신규 투표
        Vote newVote = VoteConverter.toEntity(member, place, voteType);
        Vote saved = voteRepository.save(newVote);
        return VoteConverter.toVoteInfo(saved);
    }

    /**
     * 투표를 취소(삭제)한다.
     *
     * @param memberId 회원 ID
     * @param placeId  장소 ID
     */
    @Transactional
    public void cancelVote(Long memberId, Long placeId) {
        Vote vote = voteRepository.findByMemberIdAndPlaceId(memberId, placeId)
                .orElseThrow(() -> new IllegalArgumentException("투표 기록이 존재하지 않습니다."));
        voteRepository.delete(vote);
    }
}
