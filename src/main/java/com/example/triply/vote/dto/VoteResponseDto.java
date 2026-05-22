package com.example.triply.vote.dto;

import com.example.triply.vote.entity.enums.VoteType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class VoteResponseDto {

    @Getter
    @Builder
    @AllArgsConstructor
    public static class VoteInfo {
        private Long voteId;
        private Long placeId;
        private Long memberId;
        private VoteType voteType;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class VoteSummary {
        private Long placeId;
        private long likeCount;
        private long dislikeCount;
        private double likeRatio;
        private double dislikeRatio;
        private VoteType myVoteType;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class VoteDeleted {
        private Long placeId;
        private String message;
    }
}
