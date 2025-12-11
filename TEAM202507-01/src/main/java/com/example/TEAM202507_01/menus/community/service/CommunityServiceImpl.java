package com.example.TEAM202507_01.menus.community.service;

import com.example.TEAM202507_01.menus.community.dto.CommunityDto;
import com.example.TEAM202507_01.menus.community.repository.CommunityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
//스프링(Spring)에게 "이 클래스는 비즈니스 로직을 담당하는 서비스야. 네가 관리해(Bean 등록)"라고 알려주는 명찰

@RequiredArgsConstructor
//final이 붙은 필드(변수)를 채워주는 생성자를 자동으로 만들어라"는 뜻
//why: 옛날에는 @Autowired를 썼지만, 요즘은 생성자 주입 방식이 더 안전해서 씀

@Transactional
// 클래스 안의 모든 메서드는 거래 단위로 움직인다 는 뜻
//의미: 메서드 실행 중 에러가 나면 그동안 했던 DB작업을 모두 취소하고 처음으로 되돌림
//데이터가 꼬이는 것을 방지하는 안전창치

public class CommunityServiceImpl implements CommunityService {
    //// 4. implements: "CommunityService 인터페이스에 적힌 목록을 내가 책임지고 실제로 구현

    private final CommunityMapper communityMapper;
    // 5. final: "이 변수는 한 번 정해지면 바뀌지 않는다."
    //- 위에서 @RequiredArgsConstructor가 이 친구를 위한 생성자를 만들어줌

    @Override
    @Transactional(readOnly = true)
    //READONLY = TRUE
    //이 메서드는 읽기만 하고 데이터 변경은 안해요 라고 DB에게 말해줌
    //WHY: DB가 변경 감지 안해도되네 하고 자원을 아껴서 속도가 빨라짐
    public List<CommunityDto> findAll() {
        return communityMapper.findAll();
    }

    @Override
    public CommunityDto findById(Long id) {

        // 1. 조회수 증가 (상세 보기를 할 때마다 VIEW_COUNT + 1)
        communityMapper.increaseViewCount(id);

        // 2. 데이터 가져오기
        CommunityDto community = communityMapper.findById(id);
        if (community == null) {
            throw new RuntimeException("게시글을 찾을 수 없습니다.");
            //억지로 에러를 발생시켜서 사용자에게 알려줌
        }
        return community;
    }

    @Override
    public CommunityDto save(CommunityDto community) {
        //신규등록 , 수정구분
        //ID가 NULL이다? DB에 한번도 들어간 적 없는 새글  -> INSERT
        //아이디가 있다? -> 이미 존재하는 글 -> UPDATE

        if (community.getId() == null) {
            communityMapper.save(community); // Insert 실행 (ID는 시퀀스로 생성)
        } else {
            communityMapper.update(community); // Update 실행
        }
        return community;
    }

    @Override
    public void delete(Long id) {
        communityMapper.delete(id);
    }
}