package com.example.TEAM202507_01.user.service;

import com.example.TEAM202507_01.config.jwt.TokenProvider;
import com.example.TEAM202507_01.config.property.ErrorMessagePropertySource;
import com.example.TEAM202507_01.user.dto.CreateUserDto;
import com.example.TEAM202507_01.user.dto.UserDto;
import com.example.TEAM202507_01.user.dto.UserSignInDto;
import com.example.TEAM202507_01.user.repository.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final ErrorMessagePropertySource errorMessagePropertySource;

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> findAll() {
        return userMapper.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto findById(String loginId) {
        return userMapper.findByLoginId(loginId);
    }

    @Override
    public String createToken(UserSignInDto signInDto) {
        try {
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(signInDto.getLoginId(), signInDto.getPassword());

            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

            // authentication 객체를 createToken 메소드를 통해서 JWT Token 을 생성
            return tokenProvider.createToken(authentication);

        } catch (Exception ex) {
            // 🔥 [추가] 진짜 에러 원인을 콘솔에 출력!
            ex.printStackTrace();

            // (선택) 로그가 있다면 로그로 출력
            // log.error("로그인 실패 원인: ", ex);

            throw new BadCredentialsException(errorMessagePropertySource.getBadCredentials());
        }
    }

    @Override
    public void join(CreateUserDto user) {
        if(userMapper.countByLoginId(user.getLoginId()) > 0){
            throw new RuntimeException("중복되는 아이디가 이미 있습니다.");
        }
        // 1. UUID 생성 (하이픈 포함된 표준 형식: xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx)
        String uuid = UUID.randomUUID().toString();

        // 2. DTO에 주입
        user.setId(uuid);

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // 3. Oracle DB 저장
        userMapper.save(user);
        // (이미 userDto.getUserId()에 값이 있으므로 selectKey 필요 없음)

        // 4. 🔥 [추가] 권한 정보 저장 (USER_AUTH 테이블)
        // 여기서 "ROLE_USER"라는 명찰을 강제로 달아줍니다.
        userMapper.saveAuthority(user.getLoginId(), "ROLE_USER");
    }

    @Override
    public void update(UserDto user) {
        userMapper.update(user);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
    }

    @Override
    public void delete(String loginId) {
        userMapper.delete(loginId);
    }
}