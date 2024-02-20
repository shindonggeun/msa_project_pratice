package com.ssafy.msa.domain.member.service;

import com.ssafy.msa.domain.member.dto.MemberSignupRequestDto;
import com.ssafy.msa.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;

    @Override
    public void signupMember(MemberSignupRequestDto signupRequest) {
        if (memberRepository.existsByEmail(signupRequest.getEmail())) {
            throw new RuntimeException("이미 존재하는 이메일입니다.");
        }

        memberRepository.save(signupRequest.toEntity());
    }
}
