package com.example.triply.vote.controller;

import com.example.triply.vote.dto.VoteResponseDto;
import com.example.triply.vote.service.VoteQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "투표 (일괄)")
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class VoteBulkController {

    private final VoteQueryService voteQueryService;


    @GetMapping("/votes/summary")
    @Operation(summary = "여러 장소 투표 집계 일괄 조회")
    public ResponseEntity<List<VoteResponseDto.VoteSummary>> getVoteSummaries(
            @RequestParam List<Long> placeIds,
            HttpSession session) {

        Long memberId = (Long) session.getAttribute("memberId");
        List<VoteResponseDto.VoteSummary> summaries = voteQueryService.getVoteSummaries(placeIds, memberId);
        return ResponseEntity.ok(summaries);
    }


    @GetMapping("/days/{dayId}/votes/sorted-place-ids")
    @Operation(summary = "Day 내 장소를 투표순으로 정렬한 ID 목록 조회")
    public ResponseEntity<List<Long>> getPlaceIdsSortedByVotes(
            @PathVariable Long dayId) {

        List<Long> sortedPlaceIds = voteQueryService.getPlaceIdsSortedByLikes(dayId);
        return ResponseEntity.ok(sortedPlaceIds);
    }
}
