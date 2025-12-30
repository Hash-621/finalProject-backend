package com.example.TEAM202507_01.menus.job.service;

import com.example.TEAM202507_01.menus.job.entity.JobPost;
import com.example.TEAM202507_01.menus.job.repository.JobMapper;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor  // 2. final이 붙은 필드(jobMapper)를 초기화하는 생성자를 자동으로 만들어줌
public class JobCrawlerService {

    private final JobMapper jobMapper; // DB 저장 도구

    // 사람인 검색 URL. 뒤에 검색어(keyword)를 붙여서 요청을 보낼 것임.
    private static final String SARAMIN_URL = "https://www.saramin.co.kr/zf_user/search/recruit?search_area=main&search_done=y&search_optional_item=n&searchType=search&searchword="; // 사람인 검색 주소

    // 🎓 수료일 (2026년 1월 21일). 이 날짜 이후에 마감되는 공고만 저장하려고 기준을 정함.
    private static final LocalDate GRADUATION_DATE = LocalDate.of(2026, 1, 21);

    // 🎯 목표 개수 (30개). 너무 많이 긁으면 시간도 오래 걸리고 서버에 부담되니까 제한을 둠.
    private static final int TARGET_COUNT = 30;

    @Transactional // 도중에 에러 나면 DB 저장 취소
    public String crawlAndSave() {
        int savedCount = 0; // 저장된 개수
        String keyword = "대전"; // 검색어는 '대전'으로 고정

        int currentPage = 1;
        int maxPage = 100; // 🚨 [대폭 증가] 100페이지까지 샅샅이 뒤짐

        try {
            // 한글 검색어("대전")를 URL에서 쓸 수 있는 외계어(%EB%8C%80%EC%A0%84)로 바꿈.
            String encodedKeyword = URLEncoder.encode(keyword, StandardCharsets.UTF_8);

            // [반복문 시작] 30개 채울 때까지 OR 100페이지 다 볼 때까지 계속 돎
            while (savedCount < TARGET_COUNT && currentPage <= maxPage) {

                // 최종 URL 완성: 기본주소 + 검색어 + 페이지번호
                String finalUrl = SARAMIN_URL + encodedKeyword + "&recruitPage=" + currentPage;

                // 진행 상황 로그
                System.out.println("==================================================");
                System.out.println(">>> [Page " + currentPage + "] 탐색 시작... (현재 저장: " + savedCount + "개)");

                // [Jsoup 연결]
                // Jsoup: 자바용 HTML 파서. 웹사이트에 접속해서 코드를 가져옴.
                // 1. connect(finalUrl): 해당 주소로 접속.
                // 2. userAgent(...): "나 봇 아니고 크롬 브라우저 쓴 사람이야"라고 속임 (차단 방지).
                // 3. timeout(10000): 10초 동안 응답 없으면 포기.
                // 4. get(): HTML 문서를 가져옴.
                Document doc = Jsoup.connect(finalUrl)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36") // 브라우저인 척 위장 (차단 방지)
                        .timeout(10000)
                        .get();

                // .item_recruit 클래스를 가진 요소들을 다 찾음 (공고 리스트)
                // HTML에서 class가 "item_recruit"인 요소들을 다 찾음. (이게 공고 덩어리임)
                Elements recruits = doc.select(".item_recruit");

                // 공고가 하나도 없으면? 검색 끝난 거니까 종료.
                if (recruits.isEmpty()) {
                    System.out.println(">>> 더 이상 검색 결과가 없습니다. 종료합니다.");
                    break;
                }

                int pageSkippedCount = 0; // 날짜 때문에 버린 공고 개수 세기용

                // 찾은 공고들을 하나씩(item) 꺼내서 검사함.
                for (Element item : recruits) {
                    if (savedCount >= TARGET_COUNT) break; // 30개 채웠으면 그만.

                    try {
                        // 1. 마감일 텍스트 추출.
                        // ".job_date .date" 위치에 있거나, 없으면 ".d_day" 위치에서 찾음.
                        String deadlineText = item.select(".job_date .date").text();
                        if (deadlineText == null || deadlineText.isEmpty()) deadlineText = item.select(".d_day").text();
                        if (deadlineText == null) deadlineText = "상시채용"; // 그래도 없으면 상시채용으로 침.

                        // 2. 날짜 필터링 (1/21 이전이면 가차 없이 버림)
                        if (!isAfterGraduation(deadlineText)) {
                            pageSkippedCount++; // 버린 개수 추가.
                            continue; // 다음 공고로 넘어감.
                        }

                        // 3. 제목, 회사명, 링크 추출.
                        // HTML 태그 구조를 보고 css 선택자로 텍스트를 뽑아냄.
                        String title = item.select(".job_tit a").text();
                        String company = item.select(".corp_name a").text();
                        String link = "https://www.saramin.co.kr" + item.select(".job_tit a").attr("href");

                        // 4. 지역, 경력, 학력 추출.
                        // ".job_condition span" 안에 순서대로 들어있음.
                        Elements conditions = item.select(".job_condition span");
                        String location = (conditions.size() >= 1) ? conditions.get(0).text() : "";
                        String career = (conditions.size() >= 2) ? conditions.get(1).text() : "무관";
                        String education = (conditions.size() >= 3) ? conditions.get(2).text() : "무관";

                        // 5. [중복 체크] DB에 이미 똑같은 회사, 똑같은 제목의 공고가 있으면 저장 안 함.
                        if (jobMapper.countByCompanyAndTitle(company, title) > 0) {
                            // System.out.println("   (중복) 이미 있음: " + company);
                            continue;
                        }

                        // 6. 저장할 데이터 객체(JobPost) 만들기 (빌더 패턴).
                        JobPost job = JobPost.builder()
                                .category("JOBS") // 카테고리 고정.
                                .title(title)
                                .companyName(company)
                                .companyType("무관") // 크롤링에선 알 수 없어서 무관으로 둠.
                                .description(location) // 지역 정보를 설명란에 넣음.
                                .careerLevel(career)
                                .education(education)
                                .deadline(deadlineText)
                                .link(link)
                                .isActive(1) // 활성화 상태(1).
                                .build();

                        jobMapper.insertJobPost(job);
                        savedCount++; // 저장 성공했으니 카운트 +1.
                        System.out.println("   ✅ [저장] " + company + " (" + deadlineText + ")");

                    } catch (Exception e) {
                        continue; // 하나 에러 나도 멈추지 말고 다음 거 진행.
                    }
                }

                System.out.println("   -> 페이지 결과: " + pageSkippedCount + "건 날짜 미달로 제외됨.");

                currentPage++; // 다음 페이지로.
                Thread.sleep(1000); // 1초 대기 (서버가 공격으로 오해하지 않게 쉬어줌).
            }
        } catch (Exception e) {
            e.printStackTrace(); // 에러 나면 내용 출력.
            return "오류: " + e.getMessage();
        }

        return "탐색 종료! 총 " + savedCount + "건 저장됨 (탐색한 페이지: " + (currentPage - 1) + ")";
    }

    // 날짜 텍스트를 분석해서 수료일 이후인지 판별하는 로직.
    private boolean isAfterGraduation(String text) {
        if (text.contains("상시") || text.contains("채용시")) return true; // 상시채용은 언제든 OK.
        if (text.contains("오늘") || text.contains("내일")) return false; // 오늘/내일 마감은 탈락.

        if (text.contains("~")) {
            try {
                // 정규식: 숫자/숫자 패턴을 찾음 (월/일).
                Pattern p = Pattern.compile("(\\d{1,2})/(\\d{1,2})");
                Matcher m = p.matcher(text);
                if (m.find()) {
                    int month = Integer.parseInt(m.group(1));
                    int day = Integer.parseInt(m.group(2));
                    // 1~5월은 내년(2026)으로, 나머지는 올해(2025)로 계산하는 꼼수 로직.
                    int year = (month < 6) ? 2026 : 2025;

                    LocalDate deadlineDate = LocalDate.of(year, month, day);
                    // 기준일(1/21) 이후거나 같으면 통과
                    return deadlineDate.isAfter(GRADUATION_DATE) || deadlineDate.equals(GRADUATION_DATE);
                }
            } catch (Exception e) {
                return true; // 에러 나면 그냥 통과시켜 줌 (안전하게).
            }
        }
        return true; // 패턴 없으면 통과.
    }
}