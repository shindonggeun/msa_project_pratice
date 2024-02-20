package com.ssafy.msa.domain.member.entity.enums;

public enum MemberRole {
    USER, MANAGER, ADMIN;

    public static MemberRole fromName(String roleName) {
        return MemberRole.valueOf(roleName.toUpperCase());
    }
}
