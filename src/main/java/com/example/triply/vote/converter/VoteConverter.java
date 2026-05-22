package com.example.triply.vote.converter;

import com.example.triply.member.entity.Member;
import com.example.triply.tripPlan.entity.Place;
import com.example.triply.vote.dto.VoteResponseDto;
import com.example.triply.vote.entity.Vote;
import com.example.triply.vote.entity.enums.VoteType;

public class VoteConverter {

    private VoteConverter() {
    }

    public static Vote toEntity(Member member, Place place, VoteType voteType) {
        return Vote.builder()
                .member(member)
                .place(place)
                .voteType(voteType)
                .build();
    }

    public static VoteResponseDto.VoteInfo toVoteInfo(Vote vote) {
        return VoteResponseDto.VoteInfo.builder()
                .voteId(vote.getId())
                .placeId(vote.getPlace().getId())
                .memberId(vote.getMember().getId())
                .voteType(vote.getVoteType())
                .build();
    }

    public static VoteResponseDto.VoteSummary toVoteSummary(
            Long placeId, long likeCount, long dislikeCount, VoteType myVoteType) {

        long total = likeCount + dislikeCount;
        double likeRatio = total > 0 ? (double) likeCount / total * 100 : 0;
        double dislikeRatio = total > 0 ? (double) dislikeCount / total * 100 : 0;

        return VoteResponseDto.VoteSummary.builder()
                .placeId(placeId)
                .likeCount(likeCount)
                .dislikeCount(dislikeCount)
                .likeRatio(Math.round(likeRatio * 10) / 10.0)
                .dislikeRatio(Math.round(dislikeRatio * 10) / 10.0)
                .myVoteType(myVoteType)
                .build();
    }
}
