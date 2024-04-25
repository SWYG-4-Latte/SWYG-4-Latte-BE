package com.latte.member.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.latte.member.config.auth.PrincipalDetailsService;
import com.latte.member.config.jwt.JwtAuthenticationFilter;
import com.latte.member.config.jwt.JwtAuthorizationFilter;
import com.latte.member.mapper.AuthMapper;
import com.latte.member.mapper.MemberMapper;
import org.apache.catalina.filters.CorsFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.ErrorResponse;

@Configuration
@EnableWebSecurity // 스프링 시큐리티 필터가 스프링 필터체인에 등록됨
@EnableGlobalMethodSecurity(securedEnabled = true) // secured 어노테이션 활성화
public class SecurityConfig{


    @Autowired
    private AuthMapper authMapper;

    @Autowired
    private CorsConfig corsConfig;



    @Bean
    public BCryptPasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((authorizeHttpRequest) ->
                                authorizeHttpRequest
                                        .requestMatchers("/").permitAll()
                                        .requestMatchers("/auth/login", "/auth/signup").permitAll()
                                      //  .requestMatchers("/auth/admin").hasRole("ADMIN")
                                      //  .requestMatchers("/auth/user").hasRole("USER")
                                        //.anyRequest().authenticated()
                                        .anyRequest().permitAll()
                        //.anyRequest().hasRole("ROLE_USER")
                )
                .formLogin((formLogin) ->
                        formLogin
                                .usernameParameter("mbrId")
                                .passwordParameter("password")
                                .loginPage("/auth/login")
                                .failureUrl("/auth/login?failed")
                                .loginProcessingUrl("/auth/login") // login 주소가 호출되면 시큐리티가 낚아채서 대신 로그인 진행
                                .defaultSuccessUrl("/", true)
                )
                        .logout((logout) -> logout
                                .logoutRequestMatcher(new AntPathRequestMatcher("/auth/logout"))
                                .logoutSuccessUrl("/")
                                .invalidateHttpSession(true)
                        );


        http
                .csrf((csrf) -> csrf.disable());


        return http.build();
    }



    public class MyCustomDsl extends AbstractHttpConfigurer<MyCustomDsl, HttpSecurity> {
        @Override
        public void configure(HttpSecurity http) throws Exception {
            AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
            http
                    .addFilter(corsConfig.corsFilter())
                    .addFilter(new JwtAuthenticationFilter(authenticationManager))
                    .addFilter(new JwtAuthorizationFilter(authenticationManager, authMapper));
        }
    }

}
