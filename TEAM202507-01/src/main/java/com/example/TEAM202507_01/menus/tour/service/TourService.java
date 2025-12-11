package com.example.TEAM202507_01.menus.tour.service;

import com.example.TEAM202507_01.menus.tour.dto.TourDto;
import java.util.List;

public interface TourService {
    List<TourDto> findAll();
    TourDto findById(Long id);
    TourDto save(TourDto tour);
    void delete(Long id);
}