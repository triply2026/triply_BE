package com.example.triply.vote.dto;

import com.example.triply.vote.entity.enums.VoteType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class VoteRequestDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Create {

        @NotNull(message = "투표 유형은 필수입니다.")
        private VoteType voteType;
    }
}
