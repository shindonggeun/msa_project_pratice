package com.ssafy.msa.domain.member.dto;

import com.ssafy.msa.domain.member.entity.enums.MemberRole;
import lombok.Builder;

@Builder
public record MemberInfoRecord(
        Long id,
        String email,
        String name,
        MemberRole role
) {
}
