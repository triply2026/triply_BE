package com.example.triply.vote.repository;

import com.example.triply.vote.entity.Vote;
import com.example.triply.vote.entity.enums.VoteType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VoteRepository extends JpaRepository<Vote, Long> {

    Optional<Vote> findByMemberIdAndPlaceId(Long memberId, Long placeId);

    List<Vote> findAllByPlaceId(Long placeId);

    List<Vote> findAllByPlaceIdIn(List<Long> placeIds);

    long countByPlaceIdAndVoteType(Long placeId, VoteType voteType);

    void deleteByMemberIdAndPlaceId(Long memberId, Long placeId);

    void deleteByPlaceId(Long placeId);

    void deleteByPlaceIdIn(List<Long> placeIds);

    @Query("SELECT v.place.id, v.voteType, COUNT(v) " +
            "FROM Vote v " +
            "WHERE v.place.id IN :placeIds " +
            "GROUP BY v.place.id, v.voteType")
    List<Object[]> countByPlaceIdInGroupByVoteType(@Param("placeIds") List<Long> placeIds);

    @Query("SELECT v.place.id FROM Vote v " +
            "WHERE v.place.day.id = :dayId AND v.voteType = 'LIKE' " +
            "GROUP BY v.place.id " +
            "ORDER BY COUNT(v) DESC")
    List<Long> findPlaceIdsByDayIdOrderByLikeCountDesc(@Param("dayId") Long dayId);
}
