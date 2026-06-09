package com.example.triply.websocket.service;

import lombok.Builder;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ParticipantSessionService {

    // planId → (sessionId → ParticipantInfo)
    private final ConcurrentHashMap<Long, ConcurrentHashMap<String, ParticipantInfo>> sessions
            = new ConcurrentHashMap<>();

    // sessionId → planId (역방향 조회용)
    private final ConcurrentHashMap<String, Long> sessionToPlan = new ConcurrentHashMap<>();

    public void addParticipant(Long planId, String sessionId, Long memberId, String nickname) {
        sessions.computeIfAbsent(planId, k -> new ConcurrentHashMap<>())
                .put(sessionId, ParticipantInfo.builder()
                        .memberId(memberId)
                        .nickname(nickname)
                        .sessionId(sessionId)
                        .build());
        sessionToPlan.put(sessionId, planId);
    }

    public Optional<Long> removeBySessionId(String sessionId) {
        Long planId = sessionToPlan.remove(sessionId);
        if (planId == null) return Optional.empty();
        ConcurrentHashMap<String, ParticipantInfo> planSessions = sessions.get(planId);
        if (planSessions != null) {
            planSessions.remove(sessionId);
        }
        return Optional.of(planId);
    }

    public List<ParticipantInfo> getParticipants(Long planId) {
        ConcurrentHashMap<String, ParticipantInfo> planSessions = sessions.get(planId);
        if (planSessions == null) return List.of();
        return new ArrayList<>(planSessions.values());
    }

    public Optional<Long> getMemberIdBySessionId(String sessionId) {
        Long planId = sessionToPlan.get(sessionId);
        if (planId == null) return Optional.empty();
        ConcurrentHashMap<String, ParticipantInfo> planSessions = sessions.get(planId);
        if (planSessions == null) return Optional.empty();
        ParticipantInfo info = planSessions.get(sessionId);
        return Optional.ofNullable(info).map(ParticipantInfo::getMemberId);
    }

    @Getter
    @Builder
    public static class ParticipantInfo {
        private Long memberId;
        private String nickname;
        private String sessionId;
    }
}
