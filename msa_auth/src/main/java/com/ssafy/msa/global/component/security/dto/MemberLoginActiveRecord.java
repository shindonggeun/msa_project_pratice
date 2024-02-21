package com.ssafy.msa.global.component.security.dto;

import com.ssafy.msa.domain.member.entity.enums.MemberRole;
import lombok.Builder;

@Builder
public record MemberLoginActiveRecord(
        Long id,
        String email,
        String name,
        MemberRole role
) {
}
