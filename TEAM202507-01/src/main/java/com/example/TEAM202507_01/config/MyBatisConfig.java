package com.example.TEAM202507_01.config;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import javax.sql.DataSource;

@Configuration
@MapperScan(
        basePackages = "com.example.TEAM202507_01",
        annotationClass = Mapper.class
)
public class MyBatisConfig {

    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();

        // 1. 데이터소스 연결
        sessionFactory.setDataSource(dataSource);

        // ★ [이 부분이 빠져서 에러가 났습니다!]
        // DTO/Entity들이 있는 최상위 패키지를 알려줘야 XML에서 'Community' 처럼 짧게 쓸 수 있습니다.
        sessionFactory.setTypeAliasesPackage("com.example.TEAM202507_01");

        // 2. XML 매퍼 위치 설정
        sessionFactory.setMapperLocations(
                new PathMatchingResourcePatternResolver().getResources("classpath:mappers/**/*.xml")
        );

        // 3. 카멜케이스 설정
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        configuration.setMapUnderscoreToCamelCase(true);
        sessionFactory.setConfiguration(configuration);

        // 🔥 [추가] TypeHandler가 있는 패키지 위치를 알려줍니다.
        // 이걸 추가하면 XML에서 패키지명을 다 안 쓰고 클래스 이름만 써도 됩니다.
        sessionFactory.setTypeHandlersPackage("com.example.TEAM202507_01.common.handler");

        return sessionFactory.getObject();
    }
}