package com.example.TEAM202507_01.menus.community.repository;

import com.example.TEAM202507_01.menus.community.dto.CommunityDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
//스프링에게 이 인터페이스는 MYBATIS SQL이랑 연결해 구현체는 MYBATIS가 알아서 만들어달라는 뜻
public interface CommunityMapper {
    List<CommunityDto> findAll();
    //-> XML 파일의 <select id = "findall">과 연결

    CommunityDto findById(Long id);
    //-> XML 파일의 <select id="findById">와 연결

    void save(CommunityDto community);
    void update(CommunityDto community);
    void delete(Long id);
    void increaseViewCount(Long id);

    List<CommunityDto> findByCategory(@Param("category") String category);
}