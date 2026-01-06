package com.example.TEAM202507_01.search.service;

import com.example.TEAM202507_01.menus.community.dto.CommunityDto;
import com.example.TEAM202507_01.menus.community.repository.CommunityMapper;
import com.example.TEAM202507_01.menus.hospital.dto.HospitalDto;
import com.example.TEAM202507_01.menus.hospital.repository.HospitalMapper;
import com.example.TEAM202507_01.menus.job.dto.JobDto;
import com.example.TEAM202507_01.menus.job.dto.JobUserPostDto;
import com.example.TEAM202507_01.menus.job.entity.Job;
import com.example.TEAM202507_01.menus.job.entity.JobPost;
import com.example.TEAM202507_01.menus.job.entity.JobUserPost;
import com.example.TEAM202507_01.menus.job.repository.JobMapper;
import com.example.TEAM202507_01.menus.job.repository.JobUserPostMapper;
import com.example.TEAM202507_01.menus.news.dto.NewsDto;
import com.example.TEAM202507_01.menus.news.repository.NewsMapper;
import com.example.TEAM202507_01.menus.restaurant.dto.RestaurantDto;
import com.example.TEAM202507_01.menus.restaurant.repository.RestaurantMapper;
import com.example.TEAM202507_01.menus.tour.dto.TourDto;
import com.example.TEAM202507_01.menus.tour.dto.TourPostDto;
import com.example.TEAM202507_01.menus.tour.repository.TourMapper;
import com.example.TEAM202507_01.menus.tour.repository.TourPostMapper;
import com.example.TEAM202507_01.search.document.*;
import com.example.TEAM202507_01.search.dto.SearchDto;
import com.example.TEAM202507_01.search.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final RestaurantMapper restaurantMapper;
    private final TourMapper tourMapper;
    //    private final NewsMapper newsMapper;
    private final JobMapper jobMapper;
    private final HospitalMapper hospitalMapper;
    private final CommunityMapper  communityMapper;
    private final RestaurantSearchRepository restaurantRepository;
    private final TourSearchRepository tourRepository;
    private final TourPostSearchRepository tourPostRepository;
    //    private final NewsSearchRepository newsRepository;
    private final JobSearchRepository jobRepository;
    private final JobUserPostSearchRepository jobUserPostRepository;
    private final HospitalSearchRepository hospitalRepository;
    private final CommunityPostSearchRepository communityPostRepository;
    private final JobUserPostMapper jobUserPostMapper;
    private final TourPostMapper tourPostMapper;


    @Transactional(readOnly = true)
    public SearchDto searchIntegrated(String keyword) {

        // 0. 검색어 없으면 빈 껍데기 리턴
        if (keyword == null || keyword.trim().isEmpty()) {
            return SearchDto.builder()
                    .restaurants(List.of())
                    .tours(List.of())
                    .tourPosts(List.of())
//                    .news(List.of())
                    .jobs(List.of())
                    .jobPosts(List.of())
                    .hospitals(List.of())
                    .communityPosts(List.of())
                    .build();
        }

        // 1. 식당 검색
        List<RestaurantDocument> restaurants = restaurantRepository.searchByKeyword(keyword);

        // 2. 관광지 검색
        List<TourDocument> tours = tourRepository.searchByKeyword(keyword);

        // 3. 관광지 후기 검색
        List<TourPostDocument> tourPosts = tourPostRepository.searchByKeyword(keyword);

        // 4. 뉴스 검색
//        List<NewsDocument> news = newsRepository.searchByKeyword(keyword);

        // 5. 구인구직(채용) 검색
        List<JobDocument> jobs = jobRepository.searchByKeyword(keyword);

        // 6. 개인 이력서 검색
        List<JobUserPostDocument> jobPosts = jobUserPostRepository.searchByKeyword(keyword);

        // 7. 병원 검색
        List<HospitalDocument> hospitals = hospitalRepository.searchByKeyword(keyword);

        // 8. 커뮤니티 게시글 검색
        List<CommunityPostDocument> communityPosts = communityPostRepository.searchByKeyword(keyword);

        // 🎁 [최종 포장] 모든 결과를 하나의 DTO에 담아서 리턴
        return SearchDto.builder()
                .restaurants(restaurants)
                .tours(tours)
                .tourPosts(tourPosts)
//               .news(news)
                .jobs(jobs)
                .jobPosts(jobPosts)
                .hospitals(hospitals)
                .communityPosts(communityPosts)
                .build();
    }


    // 🔥 [통합] 한 번에 모든 데이터 마이그레이션 실행
    @Transactional(readOnly = true)
    public String migrateAllData() {
        StringBuilder result = new StringBuilder();

        result.append(restaurantDtoToEs()).append("\n");
        result.append(tourDtoToEs()).append("\n");
        result.append(tourPostDtoToEs()).append("\n");
//        result.append(newsDtoToEs()).append("\n");
        result.append(jobDtoToEs()).append("\n");
        result.append(jobUserPostDtoToEs()).append("\n");
        result.append(hospitalDtoToEs()).append("\n");
        result.append(communityPostDtoToEs());

        return result.toString();
    }



    public String restaurantDtoToEs() {
// 1. DB에서 모든 데이터 가져오기
        List<RestaurantDto> dbList = restaurantMapper.findAll();

        if (dbList.isEmpty()) return "DB에 데이터가 없습니다.";

        // 2. DTO -> Document 변환
        List<RestaurantDocument> docList = dbList.stream()
                .map(dto -> RestaurantDocument.builder()
                        .id(dto.getId())          // DB ID를 그대로 씀 (중요)
                        .name(dto.getName())
                        .address(dto.getAddress())
                        .restCategory(dto.getRestCategory())
                        .bestMenu(dto.getBestMenu())
                        .menu(dto.getMenu())
                        .menuDetail(dto.getMenuDetail())
                        .phone(dto.getPhone())
                        .openTime(dto.getOpenTime())
                        .price(dto.getPrice())
                        .url(dto.getUrl())
                        .imagePath(dto.getImagePath()) // 이미지 경로도 옮김
                        .build())
                .collect(Collectors.toList());

        // 3. ES에 한방에 저장 (Bulk Insert)
        restaurantRepository.saveAll(docList);

        return "맛집: " + docList.size() + "건 완료";
    };
    // =========================================================
    // 2. 관광지 (Tour)
    // =========================================================
    public String tourDtoToEs() {
        List<TourDto> dbList = tourMapper.findAll();
        if (dbList.isEmpty()) return "관광지: 데이터 없음";

        List<TourDocument> docList = dbList.stream()
                .map(dto -> TourDocument.builder()
                        .id(dto.getId())
                        .name(dto.getName())
                        .address(dto.getAddress())
                        .description(dto.getDescription())
                        .phone(dto.getPhone())
                        .image(dto.getImage())
                        .build())
                .collect(Collectors.toList());

        tourRepository.saveAll(docList);
        return "관광지: " + docList.size() + "건 완료";
    }

    // =========================================================
    // 3. 관광지 후기 (TourPost)
    // =========================================================
    public String tourPostDtoToEs() {
        List<TourPostDto> dbList = tourPostMapper.findAll();
        if (dbList.isEmpty()) return "관광지후기: 데이터 없음";

        List<TourPostDocument> docList = dbList.stream()
                .map(dto -> TourPostDocument.builder()
                        .id(dto.getId())
                        .title(dto.getTitle())
                        .content(dto.getContent())
                        .userId(dto.getUserId())
                        .category(dto.getCategory())
                        .viewCount(dto.getViewCount())
                        .createdAt(dto.getCreatedAt())
                        .updatedAt(dto.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());

        tourPostRepository.saveAll(docList);
        return "관광지후기: " + docList.size() + "건 완료";
    }

    // =========================================================
    // 4. 뉴스 (News)
    // =========================================================
//    public String newsDtoToEs() {
//        List<NewsDto> dbList = newsMapper.findAll();
//        if (dbList.isEmpty()) return "뉴스: 데이터 없음";
//
//        List<NewsDocument> docList = dbList.stream()
//                .map(dto -> NewsDocument.builder()
//                        .title(dto.getTitle())
//                        .content(dto.getContent())
//                        .source(dto.getSource())
//                        .author(dto.getAuthor())
//                        .imageUrl(dto.getImageUrl())
//                        .publishedAt(dto.getPublishedAt())
//                        .build())
//                .collect(Collectors.toList());
//        newsRepository.saveAll(docList);
//        return "뉴스: " + docList.size() + "건 완료";
//    }

    // =========================================================
    // 5. 채용공고 (Job)
    // =========================================================
    public String jobDtoToEs() {
        List<JobPost> dbList = jobMapper.findAllSearch();
        if (dbList.isEmpty()) return "채용공고: 데이터 없음";

        List<JobDocument> docList = dbList.stream()
                .map(dto -> JobDocument.builder()
                        .id(dto.getId())
                        .category(dto.getCategory())
                        .title(dto.getTitle())
                        .companyName(dto.getCompanyName())
                        .description(dto.getDescription())
                        .companyType(dto.getCompanyType())
                        .careerLevel(dto.getCareerLevel()) // DTO 필드명 확인
                        .education(dto.getEducation())
                        .deadline(dto.getDeadline())
                        .link(dto.getLink()) // DTO의 link 필드 -> Document link
                        .isActive(dto.getIsActive())
                        .createdAt(dto.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        jobRepository.saveAll(docList);
        return "채용공고: " + docList.size() + "건 완료";
    }

    // =========================================================
    // 6. 개인 이력서 (JobUserPost)
    // =========================================================
    public String jobUserPostDtoToEs() {
        List<JobUserPost> dbList = jobUserPostMapper.findAll();
        if (dbList.isEmpty()) return "이력서: 데이터 없음";

        List<JobUserPostDocument> docList = dbList.stream()
                .map(dto -> JobUserPostDocument.builder()
                        .id(dto.getId())
                        .title(dto.getTitle())
                        .companyName(dto.getCompanyName())
                        .description(dto.getDescription())
                        .category(dto.getCategory())
                        .userId(dto.getUserId())
                        .companyType(dto.getCompanyType())
                        .careerLevel(dto.getCareerLevel())
                        .education(dto.getEducation())
                        .deadline(dto.getDeadline())
                        .createdAt(dto.getCreatedAt())
                        .isActive(dto.getIsActive())
                        .build())
                .collect(Collectors.toList());

        jobUserPostRepository.saveAll(docList);
        return "이력서: " + docList.size() + "건 완료";
    }

    // =========================================================
    // 7. 병원 (Hospital)
    // =========================================================
    public String hospitalDtoToEs() {
        List<HospitalDto> dbList = hospitalMapper.findAll();
        if (dbList.isEmpty()) return "병원: 데이터 없음";

        List<HospitalDocument> docList = dbList.stream()
                .map(dto -> HospitalDocument.builder()
                        .id(dto.getId())
                        .name(dto.getName())
                        .address(dto.getAddress())
                        .treatCategory(dto.getTreatCategory())
                        .tel(dto.getTel())
                        .editDate(dto.getEditDate())
                        .averageRating(dto.getAverageRating())
                        .reviewCount(dto.getReviewCount())
                        .build())
                .collect(Collectors.toList());

        hospitalRepository.saveAll(docList);
        return "병원: " + docList.size() + "건 완료";
    }

    // =========================================================
    // 8. 커뮤니티 (CommunityPost)
    // =========================================================
    public String communityPostDtoToEs() {
        List<CommunityDto> dbList = communityMapper.findAll(); // DTO 이름 확인 (PostDto?)
        if (dbList.isEmpty()) return "커뮤니티: 데이터 없음";

        List<CommunityPostDocument> docList = dbList.stream()
                .map(dto -> CommunityPostDocument.builder()
                        .id(dto.getId())
                        .title(dto.getTitle())
                        .content(dto.getContent())
                        .userNickname(dto.getUserNickname())
                        .userId(dto.getUserId())
                        .category(dto.getCategory())
                        .viewCount(dto.getViewCount())
                        .createdAt(dto.getCreatedAt())
                        .updatedAt(dto.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());

        communityPostRepository.saveAll(docList);
        return "커뮤니티: " + docList.size() + "건 완료";
    }
}