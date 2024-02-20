package com.ssafy.msa.domain.member.service;

import com.ssafy.msa.domain.member.dto.MemberInfoRecord;
import com.ssafy.msa.domain.member.dto.MemberLoginRequestRecord;
import com.ssafy.msa.domain.member.dto.MemberLoginResponseRecord;
import com.ssafy.msa.domain.member.dto.MemberSignupRequestDto;

public interface MemberService {
    void signupMember(MemberSignupRequestDto signupRequest);

    MemberLoginResponseRecord loginMember(MemberLoginRequestRecord loginRequest);

    MemberInfoRecord getMember(Long memberId);
}
