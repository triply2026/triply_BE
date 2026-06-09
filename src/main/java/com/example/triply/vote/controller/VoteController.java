package com.example.triply.vote.controller;

import com.example.triply.vote.dto.VoteRequestDto;
import com.example.triply.vote.dto.VoteResponseDto;
import com.example.triply.vote.service.VoteCommandService;
import com.example.triply.vote.service.VoteQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "투표")
@RequestMapping("/api/v1/places/{placeId}/votes")
@RequiredArgsConstructor
public class VoteController {

    private final VoteCommandService voteCommandService;
    private final VoteQueryService voteQueryService;

    @PostMapping
    @Operation(summary = "장소 투표 (생성/변경/토글취소)")
    public ResponseEntity<?> vote(
            @PathVariable Long placeId,
            @Valid @RequestBody VoteRequestDto.Create request,
            HttpSession session) {

        Long memberId = getMemberIdFromSession(session);

        VoteResponseDto.VoteInfo result = voteCommandService.vote(memberId, placeId, request.getVoteType());

        if (result == null) {
            // 토글 취소
            VoteResponseDto.VoteDeleted deleted = VoteResponseDto.VoteDeleted.builder()
                    .placeId(placeId)
                    .message("투표가 취소되었습니다.")
                    .build();
            return ResponseEntity.ok(deleted);
        }

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }


    @DeleteMapping
    @Operation(summary = "장소 투표 취소")
    public ResponseEntity<VoteResponseDto.VoteDeleted> cancelVote(
            @PathVariable Long placeId,
            HttpSession session) {

        Long memberId = getMemberIdFromSession(session);
        voteCommandService.cancelVote(memberId, placeId);

        VoteResponseDto.VoteDeleted response = VoteResponseDto.VoteDeleted.builder()
                .placeId(placeId)
                .message("투표가 취소되었습니다.")
                .build();
        return ResponseEntity.ok(response);
    }


    @GetMapping("/summary")
    @Operation(summary = "장소 투표 집계 조회")
    public ResponseEntity<VoteResponseDto.VoteSummary> getVoteSummary(
            @PathVariable Long placeId,
            HttpSession session) {

        Long memberId = getMemberIdFromSessionOrNull(session);
        VoteResponseDto.VoteSummary summary = voteQueryService.getVoteSummary(placeId, memberId);
        return ResponseEntity.ok(summary);
    }

    private Long getMemberIdFromSession(HttpSession session) {
        Long memberId = (Long) session.getAttribute("memberId");
        if (memberId == null) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }
        return memberId;
    }

    private Long getMemberIdFromSessionOrNull(HttpSession session) {
        return (Long) session.getAttribute("memberId");
    }
}
