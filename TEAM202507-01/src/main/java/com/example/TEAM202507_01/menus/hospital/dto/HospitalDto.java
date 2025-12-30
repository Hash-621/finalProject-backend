package com.example.TEAM202507_01.menus.hospital.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HospitalDto {
    private Long id;
    // 병원 고유 번호(PK). 21억 건이 넘을 수 있으니 int 대신 Long을 씀

    private String category;
    // 병원 종류 (예: 종합병원, 의원).

    private String name;
    //병원이름

    private String treatCategory;
    //진료과목(소분류)

    private String address;
    //병원 주소

    private String tel;
    //전화번호. 숫자로 계산할 일이 없으니 String으로 저장함 (010-1234-5678).

    private LocalDateTime editDate;
    // 정보가 마지막으로 수정된 시간.

    private Double averageRating;
    // 평점 평균. 소수점이 필요해서 Double을 씀.

    private Integer reviewCount;
    // 리뷰 개수. 정수니까 Integer.


}
