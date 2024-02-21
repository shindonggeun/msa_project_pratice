package com.ssafy.msa.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.msa.domain.member.repository.MemberRepository;
import com.ssafy.msa.global.component.security.SecurityFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * Spring Security 설정을 담당하는 클래스입니다.
 * {@link EnableMethodSecurity} 어노테이션을 통해 메소드 단위의 보안 설정을 활성화합니다.
 */
@RequiredArgsConstructor
@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {
    private final MemberRepository memberRepository;
    private final ObjectMapper objectMapper;

    /**
     * Spring Security의 HTTP 보안 설정을 구성합니다.
     *
     * @param http HttpSecurity
     * @return 구성된 SecurityFilterChain
     * @throws Exception 보안 구성 중 발생 가능한 예외
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // CORS 설정
        http.cors(cors ->
                        cors.configurationSource(corsConfigurationSource())
                )
                .httpBasic(AbstractHttpConfigurer::disable) // HTTP 기본 인증 비활성화
                .headers(header ->
                        header.frameOptions(
                                HeadersConfigurer.FrameOptionsConfig::disable // 프레임 옵션 비활성화
                        )
                )
                .authorizeHttpRequests(auth ->
                        auth.anyRequest().permitAll()   // 모든 요청에 대해 접근 허용
                )
                .formLogin(AbstractHttpConfigurer::disable) // Spring security 자체 제공 로그인 폼 비활성화
                .logout(AbstractHttpConfigurer::disable)    // Spring security 자체 제공 로그아웃 비활성화
                .addFilterBefore(securityFilter(), UsernamePasswordAuthenticationFilter.class);  // Security 필터 추가 (커스텀 필터)

        return http.build();
    }

    /**
     * WebSecurityCustomizer를 통해 웹 보안을 커스터마이징합니다.
     *
     * @return 구성된 WebSecurityCustomizer
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().anyRequest();    // 모든 요청에 대해 보안 검사 무시
    }

    /**
     * JWT 토큰을 처리하기 위한 필터를 생성합니다.
     *
     * @return JwtSecurityFilter
     */
    @Bean
    public SecurityFilter securityFilter() {
        return new SecurityFilter(memberRepository, objectMapper);
    }

    /**
     * CORS(Cross-Origin Resource Sharing) 설정을 위한 CorsConfigurationSource를 생성합니다.
     *
     * @return CorsConfigurationSource
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("*"); // 모든 오리진 허용
        configuration.addAllowedMethod("*"); // 모든 HTTP 메소드 허용
        configuration.addAllowedHeader("*"); // 모든 헤더 허용
        configuration.setAllowCredentials(true); // 쿠키 허용
        configuration.setMaxAge(3600L); // 프리플라이트 요청 캐시 시간
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * CORS 필터를 등록하기 위한 FilterRegistrationBean을 생성합니다.
     *
     * @return FilterRegistrationBean<CorsFilter>
     */
    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilterRegistrationBean() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(false); // 쿠키 사용 비활성화
        config.addAllowedOrigin("*"); // 모든 오리진 허용
        config.addAllowedHeader("*"); // 모든 헤더 허용
        config.addAllowedMethod("*"); // 모든 HTTP 메소드 허용
        config.setMaxAge(6000L); // 프리플라이트 요청 캐시 시간 설정
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        FilterRegistrationBean<CorsFilter> filterBean = new FilterRegistrationBean<>(
                new CorsFilter(source));
        filterBean.setOrder(0); // 필터 체인에서의 순서 설정
        return filterBean;
    }

    /**
     * 비밀번호 암호화를 위한 PasswordEncoder 빈을 생성합니다.
     *
     * @return PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt 알고리즘을 사용한 패스워드 암호화 객체 생성
        return new BCryptPasswordEncoder();
    }
}
