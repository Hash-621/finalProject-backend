package com.example.TEAM202507_01.user.repository;

import com.example.TEAM202507_01.user.dto.MyPageDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

@Mapper
public interface MyPageMapper {
    // 내 정보
    MyPageDto getMyInfo(String userId);
    void updateMyInfo(MyPageDto dto);

    // 작성글/댓글/즐겨찾기 (페이징 인자 3개 필수)
    List<Map<String, Object>> getMyPosts(@Param("userId") String userId, @Param("offset") int offset, @Param("size") int size);
    List<Map<String, Object>> getMyComments(@Param("userId") String userId, @Param("offset") int offset, @Param("size") int size);
    List<Map<String, Object>> getMyFavorites(@Param("userId") String userId, @Param("offset") int offset, @Param("size") int size);

    // 수정
    void updatePost(@Param("id") Long id, @Param("userId") String userId, @Param("title") String title, @Param("content") String content);
    void updateComment(@Param("id") Long id, @Param("userId") String userId, @Param("content") String content);

    // 삭제
    void deletePost(@Param("id") Long id, @Param("userId") String userId);
    void deleteComment(@Param("id") Long id, @Param("userId") String userId);
    void deleteFavorite(@Param("id") Long id, @Param("userId") String userId);
}