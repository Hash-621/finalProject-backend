package com.example.TEAM202507_01.menus.job.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobDto {
    private Long id; // 게시글 번호
    private String category; // 게시판 카테고리 (JOBS)
    private String title; // 공고 제목
    private String companyName; // 회사 이름
    private String companyType; // 회사 형태 (중소, 대기업 등)
    private String description;// 본문 내용 (크롤링 시엔 지역 정보로 씀)


    private String careerLevel; // 경력 사항 (DB 컬럼명과 매칭)

    private String education; // 학력 사항
    private String deadline; // 마감일 (날짜 계산 안 하고 문자로 저장)

    // 🚨 핵심 유지: DB의 link 데이터를 프론트엔드에선 'url'로 인식하게 함
    @JsonProperty("url")
    private String link; // 사람인 공고 원본 링크 (JSON에선 url로 나감)

    private int isActive; // 공고 진행 여부 (1: 진행중, 0: 마감)

    // 이 메서드는 careerLevel이라는 변수값을 꺼낼 때 getCareer()라는 이름으로 꺼내게 해줌.
    // 프론트엔드에서 career라는 이름으로 데이터를 찾고 있어서 만든 호환용 메서드임.
    public String getCareer() {
        return careerLevel;
    }
}