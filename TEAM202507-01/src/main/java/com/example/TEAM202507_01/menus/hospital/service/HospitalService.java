package com.example.TEAM202507_01.menus.hospital.service;

import com.example.TEAM202507_01.menus.hospital.dto.HospitalDto;
import java.util.List;

public interface HospitalService {
    // [중요] 구현체와 똑같이 반환 타입을 HospitalDto로 맞춰줍니다.
    List<HospitalDto> findAll();

    // [중요] 여기도 HospitalDto로 맞춰줍니다.
    HospitalDto findById(Long id);

    HospitalDto save(HospitalDto hospital);

    void delete(Long id);
}