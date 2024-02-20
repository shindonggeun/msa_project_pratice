package com.ssafy.msa.global.component.jwt.service;

import com.ssafy.msa.domain.member.dto.MemberInfoRecord;
import com.ssafy.msa.domain.member.dto.MemberLoginResponseRecord;
import com.ssafy.msa.domain.member.entity.Member;
import com.ssafy.msa.global.component.jwt.JwtTokenProvider;
import com.ssafy.msa.global.component.jwt.dto.TokenRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtTokenServiceImpl implements JwtTokenService {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public MemberLoginResponseRecord issueAndSaveTokens(Member member) {
        String accessToken = jwtTokenProvider.issueAccessToken(member);
        String refreshToken = jwtTokenProvider.issueRefreshToken();
        log.info("== {} 회원에 대한 토큰 발급: {}", member.getEmail(), accessToken);


        TokenRecord token = new TokenRecord(accessToken);
        MemberInfoRecord memberInfo = MemberInfoRecord.builder()
                .id(member.getId())
                .email(member.getEmail())
                .name(member.getName())
                .role(member.getRole())
                .build();

        return new MemberLoginResponseRecord(token, memberInfo); // 로그인 응답 데이터 반환
    }
}
