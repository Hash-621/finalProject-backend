package com.example.TEAM202507_01.menus.job.repository;

import com.example.TEAM202507_01.menus.job.entity.Company;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CompanyMapper {
    // 회사 이름으로 조회 (중복 확인용)
    Company findByName(String name);

    // 회사 정보 저장
    void save(Company company);
}