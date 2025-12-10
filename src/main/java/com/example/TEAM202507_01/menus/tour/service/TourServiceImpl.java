package com.example.TEAM202507_01.menus.tour.service;

import com.example.TEAM202507_01.menus.tour.entity.Tour;
import com.example.TEAM202507_01.menus.tour.repository.TourRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TourServiceImpl implements TourService {

    private final TourRepository tourRepository; // Mapper 대신 Repository 주입

    @Override
    public List<Tour> findAll() {
        return tourRepository.findAll();
    }

    @Override
    public Tour findById(Long id) {
        return tourRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 관광지를 찾을 수 없습니다. ID: " + id));
    }

    @Override
    public Tour save(Tour tour) {
        return tourRepository.save(tour);
    }

    @Override
    public void delete(Long id) {
        tourRepository.deleteById(id);
    }
}