package com.example.TEAM202507_01.config.jwt;

import com.example.TEAM202507_01.config.property.ErrorMessagePropertySource;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TokenProvider implements InitializingBean {

    private final JwtPropertySource jwtPropertySource;
    private static final String AUTH_CLAIM_KEY = "authorities";
    private Key key;
    private final ErrorMessagePropertySource errorMessagePropertySource;

    @Override
    public void afterPropertiesSet() throws Exception {
        // Secret 을 Base64로 디코딩해서 key 변수에 할당
        byte[] bytes = Decoders.BASE64.decode(jwtPropertySource.getSecret());
        key = Keys.hmacShaKeyFor(bytes);
    }

    public String createToken(Authentication authentication) {
        // 권한
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        Date now = new Date();
        Date expiration = new Date(now.getTime() + (jwtPropertySource.getExpirationSeconds())); // 만료일 계산

        return Jwts.builder()
                .issuedAt(now)
                .issuer(jwtPropertySource.getIss())
                .subject(authentication.getName())
                .expiration(expiration)
                .claim(AUTH_CLAIM_KEY, authorities)
                .signWith(key)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parser().verifyWith((SecretKey) key).build().parseSignedClaims(token).getPayload();

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTH_CLAIM_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .toList();

        User principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    public boolean isValidToken(String token) throws Exception {
        try {
            Jwts.parser().verifyWith((SecretKey) key).build().parseSignedClaims(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            throw new Exception(errorMessagePropertySource.getInvalidSignature());
        }
//        catch (SecurityException | MalformedJwtException e) {
//            throw new Exception(errorMessagePropertySource.getInvalidSignature());
//        } catch (ExpiredJwtException e) {
//            throw new Exception(errorMessagePropertySource.getExpiredToken());
//        } catch (UnsupportedJwtException e) {
//            throw new Exception(errorMessagePropertySource.getUnsupportedToken());
//        } catch (IllegalArgumentException e) {
//            throw new Exception(errorMessagePropertySource.getInvalidToken());
//        }
    }

    public String getLoginId(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject(); // 토큰 만들 때 넣었던 아이디(authentication.getName())값
    }
}

