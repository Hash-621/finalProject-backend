package com.example.TEAM202507_01.menus.hospital.controller;

import com.example.TEAM202507_01.menus.hospital.dto.HospitalDto;
import com.example.TEAM202507_01.menus.hospital.dto.HospitalMapDto;
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

    @GetMapping("/map")
    public ResponseEntity<List<HospitalMapDto>> getHospitalInfo() {
        // 서비스가 DTO 리스트를 반환하므로, 컨트롤러도 DTO 리스트를 반환해야 합니다.
        return ResponseEntity.ok(hospitalService.findInfo());
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

///전체구조 및 연결 흐름 ///

//1.사용자 (Client):
// URL: POST /api/v1/hospital
//Body: { "name": "대전 한국병원", "category": "종합병원", ... }

//2.Controller (HospitalController):
//createHospital 메서드 작동.
//JSON을 HospitalDto로 받음
//hospitalService.save(hospital) 호출

//3.Service (HospitalServiceImpl):
//save 메서드 작동.
//ID가 null인지 확인 (새 등록이므로 null임).
//hospitalMapper.save(hospital) 호출.

//4.Mapper (HospitalMapper + xml):
//XML의 <insert id="save"> 실행.
//DB에 INSERT INTO HOSPITALS ... 쿼리 날림.
//데이터 저장 완료.

//5.Return Path:
//DB -> Mapper -> Service -> Controller -> 사용자에게 "병원등록 성공" 응답.

//Controller: 프론트엔드와 대화하는 창구.

//Service: 로직을 처리하고 데이터의 형태를 가공(Entity <-> DTO)하는 공장.

//Mapper: DB 쿼리를 관리하는 창고지기.

//DTO: 각 계층을 오가는 데이터 상자.

//XML: 실제 SQL 쿼리문이 적혀있는 주문서.