package com.example.triply.ai.prompt;

import com.example.triply.ai.dto.ItineraryRequestDto;
import com.example.triply.tripPlan.entity.enums.TripStyle;
import org.springframework.stereotype.Component;

import java.time.temporal.ChronoUnit;

@Component
public class PromptBuilder {

    public String buildItineraryPrompt(ItineraryRequestDto request) {
        long tripDays = ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate()) + 1;

        StringBuilder sb = new StringBuilder();
        sb.append("당신은 전문 여행 플래너입니다. 아래 조건에 맞는 여행 일정을 JSON 형식으로 생성해주세요.\n\n");

        sb.append("**여행 정보:**\n");
        sb.append("- 여행지: ").append(request.getDestination()).append("\n");
        sb.append("- 시작일: ").append(request.getStartDate()).append("\n");
        sb.append("- 종료일: ").append(request.getEndDate()).append("\n");
        sb.append("- 여행 기간: ").append(tripDays).append("일\n");
        sb.append("- 인원수: ").append(request.getMemberCount()).append("명\n");

        if (request.getBudget() != null) {
            sb.append("- 총 예산: ").append(String.format("%,d", request.getBudget())).append("원\n");
        }
        if (request.getTripStyle() != null) {
            sb.append("- 여행 스타일: ").append(resolveTripStyleLabel(request.getTripStyle())).append("\n");
        }

        sb.append("""

                **응답 JSON 스키마 (반드시 아래 구조만 반환하세요):**
                {
                  "days": [
                    {
                      "dayNumber": 1,
                      "date": "YYYY-MM-DD",
                      "places": [
                        {
                          "name": "장소명",
                          "address": "상세 주소",
                          "category": "RESTAURANT|ACCOMMODATION|ATTRACTION|CAFE|SHOPPING|ETC",
                          "estimatedCost": 10000,
                          "stayDurationMin": 60,
                          "description": "장소에 대한 한 줄 설명"
                        }
                      ]
                    }
                  ]
                }

                **생성 규칙:**
                - 각 일자별 장소는 4~6개로 구성하세요
                - 실제 존재하는 장소와 정확한 주소를 사용하세요
                - estimatedCost는 1인 기준 원화(KRW) 정수값입니다
                - stayDurationMin은 해당 장소 체류 시간(분) 정수값입니다
                - 장소 순서는 효율적인 동선을 고려하세요
                - JSON 외 어떠한 텍스트도 포함하지 마세요
                """);

        return sb.toString();
    }

    public String buildPlaceDetailPrompt(String name, String address) {
        return """
                당신은 여행 정보 전문가입니다. 아래 장소에 대한 상세 정보를 JSON 형식으로 반환해주세요.

                **장소 정보:**
                - 장소명: """ + name + """

                - 주소: """ + address + """


                **응답 JSON 스키마 (반드시 아래 구조만 반환하세요):**
                {
                  "description": "장소 설명과 방문자 후기를 마크다운 형식으로 요약 (## 소제목 활용)",
                  "sourceUrls": ["출처 URL1", "출처 URL2"],
                  "images": ["이미지 URL1", "이미지 URL2"],
                  "reservationUrl": "공식 예약 또는 상세 페이지 URL (없으면 null)"
                }

                **작성 규칙:**
                - description은 장소 특징, 대표 메뉴/볼거리, 방문자 후기 요약을 마크다운으로 작성하세요
                - sourceUrls는 네이버 블로그, 공식 홈페이지 등 신뢰할 수 있는 실제 URL만 포함하세요
                - images는 해당 장소의 실제 이미지 URL만 포함하세요 (없으면 빈 배열)
                - reservationUrl은 네이버 예약, 공식 사이트 예약 링크를 우선하세요 (없으면 null)
                - JSON 외 어떠한 텍스트도 포함하지 마세요
                """;
    }

    private String resolveTripStyleLabel(TripStyle style) {
        return switch (style) {
            case RELAXATION -> "휴양/힐링";
            case ADVENTURE -> "모험/액티비티";
            case CULTURE -> "문화/역사 탐방";
            case FOOD -> "맛집 탐방";
            case NATURE -> "자연/생태";
            case SHOPPING -> "쇼핑";
        };
    }
}
