package com.example.TEAM202507_01.menus.hospital.controller;

import com.example.TEAM202507_01.menus.hospital.dto.HospitalDto;
import com.example.TEAM202507_01.menus.hospital.service.HospitalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/hospital")
public class HospitalController {

    private final HospitalService hospitalService;

    // 1. 목록 조회 (GET)
    // [수정]: 반환 타입을 List<Hospital> -> List<HospitalDto>로 변경
    @GetMapping
    public ResponseEntity<List<HospitalDto>> getHospitalList() {
        // 서비스가 DTO 리스트를 반환하므로, 컨트롤러도 DTO 리스트를 반환해야 합니다.
        return ResponseEntity.ok(hospitalService.findAll());
    }

    // 2. 상세 조회 (GET)
    // [수정]: 반환 타입을 Hospital -> HospitalDto로 변경
    @GetMapping("/{id}")
    public ResponseEntity<HospitalDto> getHospitalDetail(@PathVariable Long id) {
        // 서비스가 DTO를 반환하므로, 여기서도 DTO로 받아야 오류가 사라집니다.
        return ResponseEntity.ok(hospitalService.findById(id));
    }

    // 3. 등록 및 수정 (POST)
    @PostMapping
    public ResponseEntity<String> createHospital(@RequestBody HospitalDto hospitalDto) {
        // DTO 내용을 원본(Entity)으로 옮겨 담는 과정 (빌더 패턴 사용)
        HospitalDto hospital = HospitalDto.builder()
                .id(hospitalDto.getId())
                .category(hospitalDto.getCategory())
                .name(hospitalDto.getName())
                .treatCategory(hospitalDto.getTreatCategory())
                .address(hospitalDto.getAddress())
                .tel(hospitalDto.getTel())
                .build();

        hospitalService.save(hospital);
        return ResponseEntity.ok("병원등록 성공");
    }

    // 4. 삭제 (DELETE)
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteHospital(@PathVariable Long id) {
        hospitalService.delete(id);
        return ResponseEntity.ok("병원 삭제 성공");
    }
}