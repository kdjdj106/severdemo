package com.example.severdemo.security;

import com.example.severdemo.security.handler.JwtAccessDeniedHandler;
import com.example.severdemo.security.handler.JwtAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Log4j2
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
//    private final OAuth2SuccessHandler oAuth2SuccessHandler;
//    private final CustomOAuth2UserService oAuth2UserService;

    private static final String[] URL_TO_PERMIT = {
            "/member/login",
            "/member/signup",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/auth/**",
            "/api/kakao",
            "/kakao",
            "/api/oauth2/kakao"
    };

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }



    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
       http
               .csrf().disable()          //csrf설정 끔
                .sessionManagement()     //세션은 stateless방식
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .exceptionHandling()                //예외처리
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)

                .and()                //jwt를 사용하는 STATELESS방식이므로 session 사용하지 않는다고 명시
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()      //인증 진행할 uri설정
                .authorizeHttpRequests()
                .requestMatchers(HttpMethod.OPTIONS,
                        "/**").permitAll()
                .requestMatchers(URL_TO_PERMIT).permitAll()
                .anyRequest().authenticated();

        http
                .addFilterBefore(new JwtRequestFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);


        log.info("securityConfig");
        return http.build();
    }
}
