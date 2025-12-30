package com.example.TEAM202507_01.menus.hospital.service;

import com.example.TEAM202507_01.menus.hospital.dto.HospitalDto;
import com.example.TEAM202507_01.menus.hospital.dto.HospitalMapDto;

import java.util.List;

public interface HospitalService {

    // 구현체(ServiceImpl)가 반드시 만들어야 할 메서드 목록임.
    List<HospitalDto> findAll();

    List<HospitalMapDto> findInfo();

    HospitalDto findById(Long id);

    HospitalDto save(HospitalDto hospital);

    void delete(Long id);
}