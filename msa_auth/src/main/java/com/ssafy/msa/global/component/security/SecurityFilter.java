package com.ssafy.msa.global.component.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.msa.domain.member.entity.Member;
import com.ssafy.msa.domain.member.exception.MemberException;
import com.ssafy.msa.domain.member.repository.MemberRepository;
import com.ssafy.msa.global.common.dto.Message;
import com.ssafy.msa.global.component.security.dto.MemberLoginActiveRecord;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class SecurityFilter extends OncePerRequestFilter {

    private final MemberRepository memberRepository;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String memberId = request.getHeader("memberId");

        if (StringUtils.hasText(memberId)) {
            try {
                Member member = memberRepository.findById(Long.valueOf(memberId)).orElseThrow(()
                        -> new RuntimeException("해당 회원을 찾을 수 없습니다."));

                log.info("회원 ID : {}  - 요청 시도",member.getId());
                MemberLoginActiveRecord loginActive = MemberLoginActiveRecord.builder()
                        .id(member.getId())
                        .email(member.getEmail())
                        .name(member.getName())
                        .role(member.getRole())
                        .build();

                SecurityContextHolder.getContext()
                        .setAuthentication(createAuthentication(loginActive));
            } catch (MemberException e) {
                SecurityContextHolder.clearContext();
                sendError(response, e);
                return;
            }
        }


        filterChain.doFilter(request, response);
    }

    private SecurityAuthenticationToken createAuthentication(MemberLoginActiveRecord member) {
        return new SecurityAuthenticationToken(member, "",
                List.of(new SimpleGrantedAuthority(member.role().name())));
    }

    private void sendError(HttpServletResponse response, MemberException e) throws IOException {
        response.setStatus(e.getErrorCode().getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        PrintWriter writer = response.getWriter();
        writer
                .write(objectMapper.writeValueAsString(
                        Message.fail(e.getErrorCode().name(), e.getMessage())));

        writer.flush();
    }
}
