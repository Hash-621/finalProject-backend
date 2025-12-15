package com.example.TEAM202507_01.menus.tour.service;

import com.example.TEAM202507_01.menus.tour.dto.TourDto;
import com.example.TEAM202507_01.menus.tour.repository.TourMapper; // Mapper Import
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TourServiceImpl implements TourService {

    private final TourMapper tourMapper;

    @Override
    @Transactional(readOnly = true)
    public List<TourDto> findAll() {
        return tourMapper.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public TourDto findById(Long id) {
        TourDto tour = tourMapper.findById(id);
        if (tour == null) {
            throw new RuntimeException("해당 관광지를 찾을 수 없습니다. ID: " + id);
        }
        return tour;
    }

    @Override
    public TourDto save(TourDto tour) {
        if (tour.getId() == null) {
            tourMapper.save(tour); // 신규 등록
        } else {
            tourMapper.update(tour); // 수정
        }
        return tour;
    }

    @Override
    public void delete(Long id) {
        tourMapper.delete(id);
    }
}