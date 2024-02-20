package com.ssafy.msa.component.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.msa.component.jwt.exception.JwtErrorCode;
import com.ssafy.msa.component.jwt.exception.JwtException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenVerifier {
    private final JwtProps jwtProps;
    private final ObjectMapper objectMapper;

    private static final String CLAIM_EMAIL = "email";
    private static final String CLAIM_NAME = "name";
    private static final String CLAIM_ROLE = "role";


    private Claims parseToken(String token, String secretKey) {
        Claims payload;

        try {
            payload = Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                    .build()
                    .parseSignedClaims(token).getPayload();

        } catch (ExpiredJwtException e) {
            throw new JwtException(JwtErrorCode.EXPIRED_TOKEN);
        } catch (MalformedJwtException | SecurityException | IllegalArgumentException e) {
            throw new JwtException(JwtErrorCode.INVALID_TOKEN);
        }

        return payload;
    }
}
