package com.example.TEAM202507_01.user.service;

import com.example.TEAM202507_01.user.dto.MyPageDto;
import com.example.TEAM202507_01.user.repository.MyPageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class MyPageService {
    private final MyPageMapper myPageMapper;

    public MyPageDto getMyInfo(String userId) { return myPageMapper.getMyInfo(userId); }
    public void updateMyInfo(MyPageDto dto) { myPageMapper.updateMyInfo(dto); }

    public List<Map<String, Object>> getMyPosts(String userId, int offset, int size) {
        return myPageMapper.getMyPosts(userId, offset, size);
    }
    public List<Map<String, Object>> getMyComments(String userId, int offset, int size) {
        return myPageMapper.getMyComments(userId, offset, size);
    }
    public List<Map<String, Object>> getMyFavorites(String userId, int offset, int size) {
        return myPageMapper.getMyFavorites(userId, offset, size);
    }

    public void updatePost(Long id, String userId, String title, String content) {
        myPageMapper.updatePost(id, userId, title, content);
    }
    public void updateComment(Long id, String userId, String content) {
        myPageMapper.updateComment(id, userId, content);
    }

    public void deletePost(Long id, String userId) { myPageMapper.deletePost(id, userId); }
    public void deleteComment(Long id, String userId) { myPageMapper.deleteComment(id, userId); }
    public void deleteFavorite(Long id, String userId) { myPageMapper.deleteFavorite(id, userId); }
}