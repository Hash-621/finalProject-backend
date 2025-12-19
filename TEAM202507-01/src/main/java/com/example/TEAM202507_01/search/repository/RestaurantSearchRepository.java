package com.example.TEAM202507_01.search.repository;

import com.example.TEAM202507_01.search.document.RestaurantDocument;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

// 🔥 <RestaurantDocument, Long> : 우리가 저장할 객체와 ID 타입
public interface RestaurantSearchRepository extends ElasticsearchRepository<RestaurantDocument, Long> {

    // 여기에 아무것도 안 적어도 saveAll(), save(), findAll() 다 쓸 수 있습니다!
    // (부모인 ElasticsearchRepository가 이미 다 가지고 있거든요)

    // 🔥 [수정] 긴 메서드 이름 대신 @Query 사용 (띄어쓰기 문제 해결)
    // "?0"은 첫 번째 파라미터(keyword)가 들어갈 자리입니다.
    // fields: 검색할 항목들 (name^2는 '이름'에 맞으면 점수를 2배 더 주라는 뜻!)
    @Query("{" +
            "\"multi_match\": {" +
            "   \"query\": \"?0\", " +
            "   \"fields\": [\"name^2\", \"address\", \"menu\", \"bestMenu\", \"menuDetail\", \"restCategory\"], " +
            "   \"type\": \"cross_fields\", " +
            "   \"operator\": \"and\"" +
            "}" +
            "}")
    List<RestaurantDocument> searchByKeyword(String keyword);
}