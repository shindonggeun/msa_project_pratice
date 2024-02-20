package com.ssafy.msa.domain.member.dto;

import com.ssafy.msa.global.component.jwt.dto.TokenRecord;
import lombok.Builder;

@Builder
public record MemberLoginResponseRecord(TokenRecord token, MemberInfoRecord memberInfo) {
}
