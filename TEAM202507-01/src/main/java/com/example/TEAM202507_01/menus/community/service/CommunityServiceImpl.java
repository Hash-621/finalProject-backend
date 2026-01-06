package com.example.TEAM202507_01.menus.community.service;

import com.example.TEAM202507_01.cleanbot.service.CleanBotService;
import com.example.TEAM202507_01.menus.community.dto.CommentDto;
import com.example.TEAM202507_01.menus.community.dto.CommunityDto;
import com.example.TEAM202507_01.menus.community.repository.CommunityMapper;
import com.example.TEAM202507_01.user.repository.MyPageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommunityServiceImpl implements CommunityService {

    private final CommunityMapper communityMapper;
    private final MyPageMapper myPageMapper;
    private final CleanBotService cleanBotService; // 클린봇 서비스 주입

    private final Path UPLOAD_PATH = Paths.get("uploads").toAbsolutePath();

    // 1. 게시글 저장 (클린봇 필터링 적용)
    @Override
    @Transactional
    public long savePost(CommunityDto dto, List<MultipartFile> files) {
        // 🔥 [CleanBot] 제목과 내용(JSON 블록 데이터) 검사
        if (cleanBotService != null) {
            log.info("🤖 [CleanBot] 게시글 텍스트 검증 시작");
            cleanBotService.checkContent(dto.getTitle()); // 제목 검사
            cleanBotService.checkContent(dto.getContent()); // 에디터 본문 검사
        }

        // 유저 고유 ID(UUID) 조회 및 설정
        String uuid = myPageMapper.findUuidByLoginId(dto.getUserId());
        if (uuid != null) dto.setUserId(uuid);

        System.out.println(dto);
        // 게시글 DB 저장
        communityMapper.insertPost(dto);
        Long postId = dto.getId();

//        // 파일 저장 로직 (Identity 설정에 맞춤)
//        if (files != null && !files.isEmpty()) {
//            File dir = UPLOAD_PATH.toFile();
//            if (!dir.exists()) dir.mkdirs();
//
//            for (MultipartFile file : files) {
//                if (file.isEmpty()) continue;
//                try {
//                    String originalName = file.getOriginalFilename();
//                    String ext = originalName.substring(originalName.lastIndexOf("."));
//                    String savedName = UUID.randomUUID().toString() + ext;
//
//                    file.transferTo(new File(dir, savedName));
//
//                    // FILES 테이블에 저장 (Identity 컬럼 제외 쿼리 호출)
//                    communityMapper.insertFile(postId, dto.getCategory(), originalName, savedName, "/images/" + savedName);
//                } catch (IOException e) {
//                    log.error("파일 저장 중 오류 발생", e);
//                }
//            }
//        }
        return postId;
    }

    // 2. 댓글 및 답글 저장 (클린봇 필터링 적용)
    @Override
    @Transactional
    public void saveComment(CommentDto dto) {
        // 🔥 [CleanBot] 댓글/답글 내용 검사
        if (cleanBotService != null) {
            log.info("🤖 [CleanBot] 댓글 필터링 시작");
            cleanBotService.checkContent(dto.getContent());
        }

        // 유저 고유 ID(UUID) 조회 및 설정
        String uuid = myPageMapper.findUuidByLoginId(dto.getUserId());
        if(uuid != null) dto.setUserId(uuid);

        // 댓글 DB 저장 (MyBatis에서 parent_id 처리됨)
        communityMapper.insertComment(dto);
    }

    // --- 나머지 메서드 유지 ---

    @Override
    @Transactional(readOnly = true)
    public List<CommunityDto> getPostList(String category, int page, int size) {
        int offset = (page - 1) * size;
        if (category == null || "ALL".equalsIgnoreCase(category)) {
            return communityMapper.selectAllPosts(offset, size);
        }
        return communityMapper.selectPostsByCategoryPaging(category, offset, size);
    }

    @Override
    @Transactional(readOnly = true)
    public CommunityDto findPostById(Long id) {
        return communityMapper.selectPostById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getFilePathsByPostId(Long postId) {
        return communityMapper.selectFilePathsByPostId(postId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommunityDto> getOtherPostsByUser(String userId, Long currentPostId) {
        return communityMapper.selectOtherPostsByUserId(userId, currentPostId);
    }

    @Override
    public String uploadEditorImage(MultipartFile file) {
        if (file.isEmpty()) return null;
        try {
            File dir = UPLOAD_PATH.toFile();
            if (!dir.exists()) dir.mkdirs();
            String savedName = UUID.randomUUID().toString() + ".jpg";
            file.transferTo(new File(dir, savedName));
            return "/images/" + savedName;
        } catch (IOException e) {
            throw new RuntimeException("에디터 이미지 업로드 실패", e);
        }
    }

    @Override @Transactional public void deletePost(Long id) { communityMapper.deletePost(id); }
    @Override @Transactional(readOnly = true) public List<CommentDto> findCommentsByPostId(Long postId) { return communityMapper.selectCommentsByPostId(postId); }
    @Override @Transactional public void deleteComment(Long id) { communityMapper.deleteComment(id); }
    @Override @Transactional(readOnly = true) public List<CommunityDto> findPostsByCategory(String category) { return communityMapper.selectPostsByCategoryPaging(category, 0, 100); }
}