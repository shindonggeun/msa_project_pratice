package com.ssafy.msa.global.component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.msa.global.common.dto.Message;
import com.ssafy.msa.global.component.jwt.JwtTokenVerifier;
import com.ssafy.msa.global.component.jwt.exception.JwtException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {

    private final JwtTokenVerifier jwtTokenVerifier;
    private final ObjectMapper objectMapper;
    private static final String BEARER_PREFIX = "Bearer ";

    public AuthorizationHeaderFilter(JwtTokenVerifier jwtTokenVerifier, ObjectMapper objectMapper) {
        super(AuthorizationHeaderFilter.Config.class);
        this.jwtTokenVerifier = jwtTokenVerifier;
        this.objectMapper = objectMapper;
    }

    public static class Config {
        // 필터 구성 요소 (필요한 경우 사용)
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            String jwt = getJwtFrom(request);

            try {
                // JWT 토큰 검증
                Long memberId = jwtTokenVerifier.parseAccessTokenGetMemberId(jwt);

                // 추출한 memberId를 요청 헤더에 추가
                ServerHttpRequest modifiedRequest = request.mutate()
                        .header("memberId", memberId.toString())
                        .build();

                // 토큰이 유효한 경우, 필터 체인을 계속 진행
                return chain.filter(exchange.mutate().request(modifiedRequest).build());
            } catch (JwtException e) {
                // 토큰 검증 실패 시 로그 출력 및 오류 응답 반환
                log.error("JWT Token Verification Error: {}", e.getMessage());
                return sendError(exchange, e);
            }
        };
    }

    @SneakyThrows
    private Mono<Void> sendError(ServerWebExchange exchange, JwtException e) {
        ServerHttpResponse response = exchange.getResponse();
        // 상태 코드 설정
        response.setStatusCode(e.getErrorCode().getHttpStatus());
        // 콘텐츠 타입 설정
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        // 오류 메시지 생성
        String errorMessage = objectMapper.writeValueAsString(Message.fail(e.getErrorCode().name(), e.getMessage()));

        DataBufferFactory dataBufferFactory = response.bufferFactory();
        DataBuffer buffer = dataBufferFactory.wrap(errorMessage.getBytes(StandardCharsets.UTF_8));

        // 응답 본문에 오류 메시지 쓰기
        return response.writeWith(Flux.just(buffer));
    }

    private String getJwtFrom(ServerHttpRequest request) {
        String bearerToken = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
        log.info("요청 : {} / 액세스 토큰 값: {}", request.getURI(), bearerToken);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }


}
