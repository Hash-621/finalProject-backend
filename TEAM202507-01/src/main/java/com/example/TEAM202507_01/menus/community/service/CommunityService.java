package com.example.TEAM202507_01.menus.community.service;

import com.example.TEAM202507_01.menus.community.dto.CommunityDto;
import java.util.List;

public interface CommunityService {
    //4.전체 조회
    List<CommunityDto> findAll();
    //List<Community>:반환타입 community 객체가 여러 개 담긴 리스트를 돌려주겠다는 뜻
    //findall():메서드 이름

    //5.상세 조회
    CommunityDto findById(Long id);
    //long id: 게시글 번호는 숫자가 매우 커질수 있으므로 int(21억개 한계 )대신  long(900경)씀

    // 6. 저장 (등록 및 수정)
    CommunityDto save(CommunityDto community);
    // - Community: 저장된 후의 결과물(ID가 채워진 상태)을 돌려줌

    // 7. 삭제
    void delete(Long id);
    // - void: 삭제하고 나면 돌려줄 게 없으니 "없음(void)"으로 설정
}