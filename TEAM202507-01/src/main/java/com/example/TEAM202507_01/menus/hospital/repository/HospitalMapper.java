package com.example.TEAM202507_01.menus.hospital.repository; // 패키지명 확인

import com.example.TEAM202507_01.menus.hospital.dto.HospitalDto;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper // MyBatis가 구현체를 자동 생성함
public interface HospitalMapper {

    // 1. 병원 전체 목록 조회
    List<HospitalDto> findAll();

    // 2. 병원 상세 조회
    HospitalDto findById(Long id);

    // 3. 병원 등록 (Insert)
    void save(HospitalDto hospital);

    // 4. 병원 정보 수정 (Update)
    void update(HospitalDto hospital);

    // 5. 병원 삭제 (Delete)
    void delete(Long id);
}