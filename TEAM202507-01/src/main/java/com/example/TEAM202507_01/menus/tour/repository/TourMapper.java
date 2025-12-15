package com.example.TEAM202507_01.menus.tour.repository;

import com.example.TEAM202507_01.menus.tour.dto.TourDto;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface TourMapper {

    // 1. 전체 조회
    List<TourDto> findAll();

    // 2. 상세 조회
    TourDto findById(Long id);

    // 3. 등록 (Insert)
    void save(TourDto tour);

    // 4. 수정 (Update)
    void update(TourDto tour);

    // 5. 삭제 (Delete)
    void delete(Long id);
}