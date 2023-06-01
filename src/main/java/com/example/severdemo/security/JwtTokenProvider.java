package com.example.severdemo.security;


import com.example.severdemo.domain.token.TokenDTO;
import com.example.severdemo.domain.user.Role;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Log4j2
public class JwtTokenProvider {

    private final Key encodedKey;
    private static final String BEARER_TYPE = "Bearer";
    private static final Long accessTokenValidationTime = 30 * 60 * 1000L;   //30분
    private static final Long refreshTokenValidationTime = 7 * 24 * 60 * 60 * 1000L;  //7일

    private String secretKey;
    public JwtTokenProvider(@Value ("${spring.jwt.secret}") String secretKey) {
        byte[] keyBytes = Base64.getDecoder().decode(secretKey);
        this.encodedKey = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * accessToken과 refreshToken을 생성함
     * @param subject
     * @return TokenDTO
     * subject는 Form Login방식의 경우 userId, Social Login방식의 경우 email
     */
    public TokenDTO createTokenDTO(String subject, List<Role> roles) {

        //권한을 하나의 String으로 합침
        String authority = roles.stream().map(Role::getType).collect(Collectors.joining(","));

        //토큰 생성시간
        Instant now = Instant.from(OffsetDateTime.now());
        //accessToken 만료시간
        Instant refreshTokenExpirationDate = now.plusMillis(refreshTokenValidationTime);

        //accessToken 생성
        String accessToken = Jwts.builder()
                .setSubject(subject)
                .claim("roles", authority)
                .setExpiration(Date.from(now.plusMillis(accessTokenValidationTime)))
                .signWith(encodedKey)
                .compact();

        //refreshToken 생성
        String refreshToken = Jwts.builder()
                .setExpiration(Date.from(now.plusMillis(refreshTokenValidationTime)))
                .signWith(encodedKey)
                .compact();

        //TokenDTO에 두 토큰을 담아서 반환
        return TokenDTO.builder()
                .tokenType(BEARER_TYPE)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .duration(Duration.ofMillis(refreshTokenValidationTime))
                .build();
    }

    /**
     * UsernamePasswordAuthenticationToken으로 보내 인증된 유저인지 확인
     * @param accessToken
     * @return Authentication
     * @throws ExpiredJwtException
     */
    public Authentication getAuthentication(String accessToken) throws ExpiredJwtException {
        Claims claims = Jwts.parserBuilder().setSigningKey(encodedKey).build().parseClaimsJws(accessToken).getBody();

        if(claims.get("roles") == null) {
            throw new RuntimeException("권한정보가 없는 토큰입니다.");
        }

        Collection<? extends GrantedAuthority> roles = Arrays.stream(claims.get("roles").toString().split(",")).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        UserDetails user = new User(claims.getSubject(), "", roles);
        return new UsernamePasswordAuthenticationToken(user, "", roles);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(encodedKey).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }
}
