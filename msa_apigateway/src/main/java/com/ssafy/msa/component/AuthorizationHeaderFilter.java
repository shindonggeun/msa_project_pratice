package com.ssafy.msa.component;

import com.ssafy.msa.component.jwt.JwtTokenVerifier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {

    private final JwtTokenVerifier jwtTokenVerifier;
    private static final String BEARER_PREFIX = "Bearer ";

    public AuthorizationHeaderFilter(JwtTokenVerifier jwtTokenVerifier) {
        super(AuthorizationHeaderFilter.Config.class);
        this.jwtTokenVerifier = jwtTokenVerifier;
    }

    public static class Config {
        // 필터 구성 요소 (필요한 경우 사용)
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            String jwt = getJwtFrom(request);
            if (jwt == null) {
                return onError(exchange, "Authorization header is missing or invalid", HttpStatus.UNAUTHORIZED);
            }

            try {
                // JWT 토큰 검증
                Long memberId = jwtTokenVerifier.parseAccessTokenGetMemberId(jwt);

                // 추출한 memberId를 요청 헤더에 추가
                ServerHttpRequest modifiedRequest = request.mutate()
                        .header("memberId", memberId.toString())
                        .build();

                // 토큰이 유효한 경우, 필터 체인을 계속 진행
                return chain.filter(exchange.mutate().request(modifiedRequest).build());
            } catch (Exception e) {
                // 토큰 검증 실패 시 로그 출력 및 오류 응답 반환
                log.error("JWT Token Verification Error: {}", e.getMessage());
                return onError(exchange, "Invalid JWT Token", HttpStatus.UNAUTHORIZED);
            }
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        log.error("onError");
        return response.setComplete();
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
