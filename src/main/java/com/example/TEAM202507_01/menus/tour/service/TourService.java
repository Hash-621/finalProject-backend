package com.example.TEAM202507_01.menus.tour.service;

import com.example.TEAM202507_01.menus.tour.entity.Tour;
import java.util.List;

public interface TourService {

    // 전체 조회
    List<Tour> findAll();

    // 상세 조회
    Tour findById(Long id);

    // 등록 및 수정
    Tour save(Tour tour);

    // 삭제
    void delete(Long id);
}