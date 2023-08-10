package com.grablunchtogether.configuration.springSecurity;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@SuppressWarnings("deprecation")
@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void configure(HttpSecurity http) throws Exception {

        http.csrf().disable();
        http.headers().frameOptions().sameOrigin();

        http.authorizeRequests()
                .antMatchers("/api/user/signup",
                        "/api/user/signup/simple",
                        "/api/user/login",
                        "/api/user/resetPassword",
                        "/swagger-ui/index.html",
                        "/swagger-ui/**",
                        "/v2/api-docs",
                        "/swagger-resources/**",
                        "/webjars/**")
                .permitAll();

        http.authorizeRequests()
                .anyRequest()
                .authenticated()
                .and()
                .addFilterBefore(new JwtTokenFilter(jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class);
    }
}