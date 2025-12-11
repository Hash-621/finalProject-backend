package com.example.TEAM202507_01.menus.hospital.service;

import com.example.TEAM202507_01.menus.hospital.dto.HospitalDto;
import com.example.TEAM202507_01.menus.hospital.repository.HospitalMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
// [의미]: 스프링에게 "나 일하는 직원(Bean)이야"라고 등록표를 붙이는 것
// [왜 썼나]: 이게 없으면 컨트롤러가 이 파일을 못 찾아서 에러

@RequiredArgsConstructor
// [의미]: "필수 재료(final 붙은 변수)를 채워주는 생성자를 대신 만들어줘" (Lombok 기능)
// [왜 썼나]: 생성자 코드를 직접 치기 귀찮아서 씀  (의존성 주입을 편하게 하기 위함)

@Transactional
// [의미]: "여기서 일어나는 일은 '전부 되거나', '전부 안 되거나' 둘 중 하나."
// [왜 썼나]: 데이터를 저장하다가 에러가 나면, 저장하기 전 상태로 깔끔하게 되돌리기(Rollback) 위해서

public class HospitalServiceImpl implements HospitalService {
// [implements]: "HospitalService라는 메뉴판(인터페이스)에 있는 메뉴를 내가 책임지고 요리할게"라는 약속

    private final HospitalMapper hospitalMapper;
    // [final]: "이 매퍼는 한 번 정해지면 절대 바뀌지 않음." (안전장치)

    // ==========================================
    // 1. 전체 목록 조회 (Stream 문법 사용)
    // ==========================================
    @Override
    @Transactional(readOnly = true)
    // [readOnly=true]: "난 읽기만 할 거니까 감시 ㄴㄴ ." (DB 성능 최적화)
    public List<HospitalDto> findAll() {
        return hospitalMapper.findAll() // 1. DB에서 Hospital(원본) 리스트를 가져옴
                .stream() // 2. [stream]: 리스트 데이터를 '수도꼭지에서 물 흐르듯' 하나씩 흘려보냄
                // [왜 썼나]: for문보다 코드가 간결하고, 데이터 가공(변환)에 유리

                .map(this::convertToDto)
                // 3. [map]: 흘러오는 데이터를 하나씩 잡아서 '모양을 바꿈
                //    - Hospital(원본) -> HospitalDto(포장된 데이터)로 변환
                //    - this::convertToDto : "아래에 있는 convertToDto라는 메서드를 써서 바꿔라"라는 뜻

                .collect(Collectors.toList());
        // 4. [collect]: 변환된 데이터들을 다시 모아서 새로운 'List'로 만듬
    }

    // ==========================================
    // 2. 상세 조회
    // ==========================================
    @Override
    @Transactional(readOnly = true)
    public HospitalDto findById(Long id) {
        HospitalDto hospital = hospitalMapper.findById(id);
        // DB에 다녀옵니다.

        if (hospital == null) {
            throw new RuntimeException("병원을 찾을 수 없습니다.");
            // [throw]: "에러를 던진다!" 프로그램 흐름을 끊고 사용자에게 "없어요!"라고 알림
        }
        return convertToDto(hospital);
        // 가져온 원본 데이터를 화면용(DTO)으로 바꿔서 내보냄
    }

    // ==========================================
    // 3. 저장 (등록/수정)
    // ==========================================
    @Override
    public HospitalDto save(HospitalDto hospital) {
        // [로직 분석]: ID가 없으면 새 병원, 있으면 기존 병원 수정
        if (hospital.getId() == null) {
            hospitalMapper.save(hospital); // INSERT 실행
        } else {
            // hospitalMapper.update(hospital); // UPDATE 실행 (필요 시 주석 해제)
        }
        return hospital;
    }

    // [추가] 인터페이스의 delete 메서드 구현 (이게 없으면 추상 메서드 오류가 납니다)
    @Override
    public void delete(Long id) {
        hospitalMapper.delete(id);
    }

    // ==========================================
    // 4. 변환 메서드 (Entity -> DTO)
    // ==========================================
    private HospitalDto convertToDto(HospitalDto hospital) {
        // [private]: "이 메서드는 이 클래스 안에서만 쓸 거임." (외부 공개 X)

        Double averageRating = 0.0; // [Double]: 소수점 숫자 (평점)
        Integer reviewCount = 0;    // [Integer]: 정수 숫자 (리뷰 개수)
        // [참고]: 나중에 여기다 리뷰 테이블 조회해서 평균 내는 코드를 넣으면 됨

        // [Builder 패턴]: 생성자(new) 대신 이름표를 붙여서 객체를 만드는 방식.
        // 순서 헷갈릴 일이 없고 가독성이 좋음
        return HospitalDto.builder()
                .id(hospital.getId())
                .category(hospital.getCategory())
                .name(hospital.getName())
                // ... (나머지 필드 복사)
                .averageRating(averageRating) // 계산된 평점 탑재
                .reviewCount(reviewCount)     // 계산된 리뷰 수 탑재
                .build(); // "조립 끝! 객체 내놔."
    }
}