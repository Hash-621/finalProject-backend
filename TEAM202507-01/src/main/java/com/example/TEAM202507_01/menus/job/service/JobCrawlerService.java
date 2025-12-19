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
@RequiredArgsConstructor
public class JobCrawlerService {

    private final JobMapper jobMapper;
    private static final String SARAMIN_URL = "https://www.saramin.co.kr/zf_user/search/recruit?search_area=main&search_done=y&search_optional_item=n&searchType=search&searchword=";

    // 🎓 수료일 고정 (이 날짜 이후 마감만 저장)
    private static final LocalDate GRADUATION_DATE = LocalDate.of(2026, 1, 21);

    // 🎯 목표 개수 (30개)
    private static final int TARGET_COUNT = 30;

    @Transactional
    public String crawlAndSave() {
        int savedCount = 0;
        String keyword = "대전";

        int currentPage = 1;
        int maxPage = 100; // 🚨 [대폭 증가] 100페이지까지 샅샅이 뒤짐

        try {
            String encodedKeyword = URLEncoder.encode(keyword, StandardCharsets.UTF_8);

            // 목표 채울 때까지 or 100페이지 다 볼 때까지 반복
            while (savedCount < TARGET_COUNT && currentPage <= maxPage) {

                String finalUrl = SARAMIN_URL + encodedKeyword + "&recruitPage=" + currentPage;

                // 진행 상황 로그
                System.out.println("==================================================");
                System.out.println(">>> [Page " + currentPage + "] 탐색 시작... (현재 저장: " + savedCount + "개)");

                Document doc = Jsoup.connect(finalUrl)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                        .timeout(10000)
                        .get();

                Elements recruits = doc.select(".item_recruit");

                if (recruits.isEmpty()) {
                    System.out.println(">>> 더 이상 검색 결과가 없습니다. 종료합니다.");
                    break;
                }

                int pageSkippedCount = 0; // 이 페이지에서 몇 개나 걸러졌는지

                for (Element item : recruits) {
                    if (savedCount >= TARGET_COUNT) break;

                    try {
                        // 1. 마감일 파싱
                        String deadlineText = item.select(".job_date .date").text();
                        if (deadlineText == null || deadlineText.isEmpty()) deadlineText = item.select(".d_day").text();
                        if (deadlineText == null) deadlineText = "상시채용";

                        // 2. 날짜 필터링 (1/21 이전이면 가차 없이 버림)
                        if (!isAfterGraduation(deadlineText)) {
                            pageSkippedCount++;
                            continue;
                        }

                        // 3. 정보 파싱
                        String title = item.select(".job_tit a").text();
                        String company = item.select(".corp_name a").text();
                        String link = "https://www.saramin.co.kr" + item.select(".job_tit a").attr("href");

                        Elements conditions = item.select(".job_condition span");
                        String location = (conditions.size() >= 1) ? conditions.get(0).text() : "";
                        String career = (conditions.size() >= 2) ? conditions.get(1).text() : "무관";
                        String education = (conditions.size() >= 3) ? conditions.get(2).text() : "무관";

                        // 4. 중복 체크 (DB에 있으면 버림)
                        if (jobMapper.countByCompanyAndTitle(company, title) > 0) {
                            // System.out.println("   (중복) 이미 있음: " + company);
                            continue;
                        }

                        // 5. 저장
                        JobPost job = JobPost.builder()
                                .category("JOBS")
                                .title(title)
                                .companyName(company)
                                .companyType("무관")
                                .description(location)
                                .careerLevel(career)
                                .education(education)
                                .deadline(deadlineText)
                                .link(link)
                                .isActive(1)
                                .build();

                        jobMapper.insertJobPost(job);
                        savedCount++;
                        System.out.println("   ✅ [저장] " + company + " (" + deadlineText + ")");

                    } catch (Exception e) {
                        continue;
                    }
                }

                System.out.println("   -> 페이지 결과: " + pageSkippedCount + "건 날짜 미달로 제외됨.");

                currentPage++;
                Thread.sleep(1000); // 1초 대기 (서버 차단 방지용 필수)
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "오류: " + e.getMessage();
        }

        return "탐색 종료! 총 " + savedCount + "건 저장됨 (탐색한 페이지: " + (currentPage - 1) + ")";
    }

    // 📅 날짜 판별 로직 (1월 21일 기준)
    private boolean isAfterGraduation(String text) {
        if (text.contains("상시") || text.contains("채용시")) return true;
        if (text.contains("오늘") || text.contains("내일")) return false;

        if (text.contains("~")) {
            try {
                Pattern p = Pattern.compile("(\\d{1,2})/(\\d{1,2})");
                Matcher m = p.matcher(text);
                if (m.find()) {
                    int month = Integer.parseInt(m.group(1));
                    int day = Integer.parseInt(m.group(2));
                    // 1월~5월은 2026년으로, 나머지는 2025년으로 처리
                    int year = (month < 6) ? 2026 : 2025;

                    LocalDate deadlineDate = LocalDate.of(year, month, day);
                    // 기준일(1/21) 이후거나 같으면 통과
                    return deadlineDate.isAfter(GRADUATION_DATE) || deadlineDate.equals(GRADUATION_DATE);
                }
            } catch (Exception e) {
                return true;
            }
        }
        return true;
    }
}