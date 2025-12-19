package com.example.TEAM202507_01;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.apache.ibatis.annotations.Mapper;

@MapperScan(
        basePackages = "com.example.TEAM202507_01",
        annotationClass = Mapper.class
)
@SpringBootApplication
// 아래 줄을 추가해서 스프링에게 "이 패키지 아래에 있는 건 다 찾아봐!"라고 강제로 시킵니다.

public class Team20250701Application {

    public static void main(String[] args) {
        SpringApplication.run(Team20250701Application.class, args);
    }
}