package com.ssafy.msa.domain.member.service;

import com.ssafy.msa.domain.member.dto.MemberInfoRecord;
import com.ssafy.msa.domain.member.dto.MemberLoginRequestRecord;
import com.ssafy.msa.domain.member.dto.MemberLoginResponseRecord;
import com.ssafy.msa.domain.member.dto.MemberSignupRequestDto;
import com.ssafy.msa.domain.member.entity.Member;
import com.ssafy.msa.domain.member.repository.MemberRepository;
import com.ssafy.msa.global.component.jwt.service.JwtTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    private final JwtTokenService jwtTokenService;

    @Override
    public void signupMember(MemberSignupRequestDto signupRequest) {
        if (memberRepository.existsByEmail(signupRequest.getEmail())) {
            throw new RuntimeException("이미 존재하는 이메일입니다.");
        }

        memberRepository.save(signupRequest.toEntity());
    }

    @Override
    public MemberLoginResponseRecord loginMember(MemberLoginRequestRecord loginRequest) {
        Member member = memberRepository.findByEmail(loginRequest.email()).orElseThrow(()
        -> new RuntimeException("해당 회원을 찾을 수 없습니다."));

        // Spring security PasswordEncoder 이용해서 비밀번호 검증 로직 이용

        return jwtTokenService.issueAndSaveTokens(member);
    }

    @Override
    public MemberInfoRecord getMember(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(()
        -> new RuntimeException("해당 회원을 찾을 수 없습니다."));

        return MemberInfoRecord.builder() // 회원 정보 반환
                .id(member.getId())
                .email(member.getEmail())
                .name(member.getName())
                .role(member.getRole())
                .build();
    }
}
