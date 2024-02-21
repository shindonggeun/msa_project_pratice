package com.ssafy.msa.global.component.jwt;

import com.ssafy.msa.global.component.jwt.exception.JwtErrorCode;
import com.ssafy.msa.global.component.jwt.exception.JwtException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenVerifier {

    @Value("${jwt.accessKey}")
    private String accessKey;

    // 토큰을 이용해서 회원 아이디 파싱
    public Long parseAccessTokenGetMemberId(String accessToken) {
        Claims payload = parseToken(accessToken, accessKey);

        return Long.valueOf(payload.getId());
    }

    private Claims parseToken(String token, String secretKey) {
        Claims payload;

        try {
            payload = Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                    .build()
                    .parseSignedClaims(token).getPayload();

        } catch (ExpiredJwtException e) {
            throw new JwtException(JwtErrorCode.EXPIRED_TOKEN);
        } catch (MalformedJwtException | SecurityException | IllegalArgumentException  e) {
            throw new JwtException(JwtErrorCode.INVALID_TOKEN);
        } catch (SignatureException e) {
            throw new JwtException(JwtErrorCode.SIGNATURE_INVALID);
        }

        return payload;
    }
}
