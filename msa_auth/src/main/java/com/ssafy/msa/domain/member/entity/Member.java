package com.ssafy.msa.domain.member.entity;

import com.ssafy.msa.domain.member.entity.enums.MemberRole;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Entity
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String password;

    private String name;

    @Enumerated(EnumType.STRING)
    private MemberRole role;

//    @Enumerated(EnumType.STRING)
//    @Column(name = "provider")
//    private OAuthDomain oAuthDomain;

}
