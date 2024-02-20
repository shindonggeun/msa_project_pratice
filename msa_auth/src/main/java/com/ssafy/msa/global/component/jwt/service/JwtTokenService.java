package com.ssafy.msa.global.component.jwt.service;

import com.ssafy.msa.domain.member.dto.MemberLoginResponseRecord;
import com.ssafy.msa.domain.member.entity.Member;

public interface JwtTokenService {
    MemberLoginResponseRecord issueAndSaveTokens(Member member);
}
