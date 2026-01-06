package com.example.TEAM202507_01.user.service;

import com.example.TEAM202507_01.config.jwt.TokenProvider;
import com.example.TEAM202507_01.config.property.ErrorMessagePropertySource;
import com.example.TEAM202507_01.menus.mailgun.service.mailService;
import com.example.TEAM202507_01.user.dto.*;
import com.example.TEAM202507_01.user.repository.UserMapper;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    @Value("${jwt.secret}")
    private String jwtSecret;
    private SecretKey secretKey;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final ErrorMessagePropertySource errorMessagePropertySource;
    private final mailService mailService;
    private final AuthenticationManager authenticationManager;
    private final StringRedisTemplate redisTemplate;


    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

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
        // 1. DB에서 해당 아이디의 유저 정보를 직접 가져와봅니다. (Mapper 사용)
        try {
            // 1. 아이디/비번 토큰 생성
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(signInDto.getLoginId(), signInDto.getPassword());

            // 2. 🔥 [수정] 주입받은 매니저로 인증 시도
            // (이제 SecurityConfig의 PasswordEncoder 설정을 자동으로 인식합니다)
            Authentication authentication = authenticationManager.authenticate(authenticationToken);

            // 3. 토큰 생성 및 반환
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
        if (userMapper.countByLoginId(user.getLoginId()) > 0) {
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

    @Override
    public void getTokenForFindID(String addr, String value) {
        mailService.sendFindIdMail(addr, value);
        if (redisTemplate.opsForValue().get(addr) != null) {
            redisTemplate.delete(addr);
        }
        redisTemplate.opsForValue().set(addr, value, Duration.ofMinutes(3));
    }

    @Override
    public String findUserId(FindUserIdDto findUserIdDto) {
        String userMail = findUserIdDto.getEmail();
        String inputToken = findUserIdDto.getToken();
        try {
            String token = redisTemplate.opsForValue().get(userMail);

            if (inputToken.isEmpty()) {
                throw new NullPointerException("token is null");
            }
            if (inputToken.equals(token)) {
                redisTemplate.delete(userMail);
                return userMapper.findRostId(findUserIdDto);
            }
        } catch (NullPointerException e) {
            if (e.getMessage() != null) {
                System.err.println("숫자를 입력해주세요");
                return "빈 토큰";
            }
            return "토큰 불일치";
        } catch (Exception e) {
            System.err.println("서버 오류 발생" + e.getMessage());
            return "서버 오류 발생";
        }
        return null;
    }

    @Override
    public void getResetPw(ResetPasswordDto resetPasswordDto) {
        int exitCount = userMapper.resetPw(resetPasswordDto);
        if (exitCount == 1) {
            String resetToken = UUID.randomUUID().toString();
            redisTemplate.opsForValue().set(resetPasswordDto.getEmail(), resetToken, Duration.ofMinutes(3));
            mailService.sendResetPwMail(resetPasswordDto.getEmail(), resetToken);
        } else if (exitCount > 1) {
            System.err.println("아이디가 두개 이상 조회됩니다. 관리자에게 문의 하세요");
        } else {
            System.err.println("예기치 않은 에러");
        }
    }

    @Override
    public boolean resetPw(String token, String email) {
        String inner = redisTemplate.opsForValue().get(email);
        if (token.equals(inner)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void updatePw(UpdatePwDto updatePwDto) throws RuntimeException {

        if (updatePwDto.getToken().equals(redisTemplate.opsForValue().get(updatePwDto.getEmail()))) {
            redisTemplate.delete(updatePwDto.getEmail());
            updatePwDto.setPassword(passwordEncoder.encode(updatePwDto.getPassword()));
            userMapper.updatePw(updatePwDto);
        } else {
            throw new RuntimeException("토큰이 일치 하지 않습니다.");
        }

    }

    @Override
    public boolean checkIdAvailability(String loginId) {
        // 아이디로 조회된 개수 가져오기
        int count = userMapper.countByLoginId(loginId);
        // 개수가 0이면 존재하지 않는 것이므로 true(사용 가능) 반환
        return count == 0;
    }

    @Override
    public String checkEmail(String email) {
        int count = userMapper.countByEmail(checkEmailDto.getEmail());
        if(count > 0) {

        }

    }
}