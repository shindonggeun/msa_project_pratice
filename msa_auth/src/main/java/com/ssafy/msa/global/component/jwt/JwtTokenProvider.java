package com.ssafy.msa.global.component.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.msa.domain.member.entity.Member;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    private final JwtProps jwtProps;

    private static final String CLAIM_EMAIL = "email";
    private static final String CLAIM_NAME = "name";
    private static final String CLAIM_ROLE = "role";

    // 액세스 토큰 발급
    public String issueAccessToken(Member member) {

        Claims claims = Jwts.claims()
                .id(String.valueOf(member.getId()))
                .add(CLAIM_EMAIL, member.getEmail())
                .add(CLAIM_NAME, member.getName())
                .add(CLAIM_ROLE, member.getRole())
                .build();

        return issueToken(claims, jwtProps.accessExpiration(), jwtProps.accessKey());
    }

    // 리프레쉬 토큰 발급
    public String issueRefreshToken() {
        return issueToken(null, jwtProps.refreshExpiration(), jwtProps.refreshKey());
    }

    private String issueToken(Claims claims, Duration expiration, String secretKey) {
        Date now = new Date();

        return Jwts.builder()
                .claims(claims)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expiration.toMillis()))
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .compact();
    }

}
