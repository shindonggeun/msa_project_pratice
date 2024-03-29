package com.ssafy.msa.domain.member.controller;

import com.ssafy.msa.domain.member.dto.MemberInfoRecord;
import com.ssafy.msa.domain.member.dto.MemberLoginRequestRecord;
import com.ssafy.msa.domain.member.dto.MemberLoginResponseRecord;
import com.ssafy.msa.domain.member.dto.MemberSignupRequestDto;
import com.ssafy.msa.domain.member.service.MemberService;
import com.ssafy.msa.global.common.dto.Message;
import com.ssafy.msa.global.component.security.dto.MemberLoginActiveRecord;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity<Message<Void>> signupMember(@Valid @RequestBody MemberSignupRequestDto signupRequest) {
        memberService.signupMember(signupRequest);
        return ResponseEntity.ok().body(Message.success());
    }

    @PostMapping("/login")
    public ResponseEntity<Message<MemberLoginResponseRecord>> loginMember(@RequestBody MemberLoginRequestRecord loginRequest,
                                                                          HttpServletResponse response) {
        MemberLoginResponseRecord loginResponse = memberService.loginMember(loginRequest);
        // JWT 토큰을 쿠키에 저장
        Cookie accessTokenCookie = new Cookie("accessToken", loginResponse.token().accessToken());
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(3600); // 60분(3600초)으로 설정 (3600)
        response.addCookie(accessTokenCookie);
        return ResponseEntity.ok().body(Message.success(loginResponse));
    }

    @GetMapping("/get")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    public ResponseEntity<Message<MemberInfoRecord>> getMember(@AuthenticationPrincipal MemberLoginActiveRecord loginActive) {
        MemberInfoRecord info = memberService.getMember(loginActive.id());
        return ResponseEntity.ok().body(Message.success(info));
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    public ResponseEntity<Message<Void>> deleteMember(@AuthenticationPrincipal MemberLoginActiveRecord loginActive) {
        memberService.deleteMember(loginActive.id());
        return ResponseEntity.ok().body(Message.success());
    }
}
