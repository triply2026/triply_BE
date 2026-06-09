package com.example.triply.websocket.service;

import lombok.Builder;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class PlaceEditLockService {

    // placeId → EditLockInfo
    private final ConcurrentHashMap<Long, EditLockInfo> locks = new ConcurrentHashMap<>();

    // memberId → List<placeId> (역방향 조회용)
    private final ConcurrentHashMap<Long, List<Long>> memberLocks = new ConcurrentHashMap<>();

    public boolean tryAcquire(Long placeId, Long memberId, String nickname) {
        EditLockInfo newLock = EditLockInfo.builder()
                .placeId(placeId)
                .memberId(memberId)
                .nickname(nickname)
                .acquiredAt(LocalDateTime.now())
                .build();
        boolean acquired = locks.putIfAbsent(placeId, newLock) == null;
        if (acquired) {
            memberLocks.computeIfAbsent(memberId, k -> new ArrayList<>()).add(placeId);
        }
        return acquired;
    }

    public boolean release(Long placeId, Long memberId) {
        EditLockInfo lock = locks.get(placeId);
        if (lock == null || !lock.getMemberId().equals(memberId)) return false;
        locks.remove(placeId);
        List<Long> held = memberLocks.get(memberId);
        if (held != null) held.remove(placeId);
        return true;
    }

    public List<Long> releaseAllByMember(Long memberId) {
        List<Long> held = memberLocks.remove(memberId);
        if (held == null) return List.of();
        held.forEach(locks::remove);
        return held;
    }

    public Optional<EditLockInfo> getLock(Long placeId) {
        return Optional.ofNullable(locks.get(placeId));
    }

    public Map<Long, EditLockInfo> getLocksForPlaces(List<Long> placeIds) {
        return placeIds.stream()
                .filter(locks::containsKey)
                .collect(Collectors.toMap(id -> id, locks::get));
    }

    @Getter
    @Builder
    public static class EditLockInfo {
        private Long placeId;
        private Long memberId;
        private String nickname;
        private LocalDateTime acquiredAt;
    }
}
