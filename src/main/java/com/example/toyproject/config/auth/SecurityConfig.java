package com.example.toyproject.config.auth;

import com.example.toyproject.domain.user.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@RequiredArgsConstructor
@Configuration
public class SecurityConfig  {
    private final CustomOAuth2UserService customOAuth2UserService;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws  Exception{
       http.csrf().disable().headers().frameOptions().disable();
       http.authorizeHttpRequests()
               .requestMatchers("/", "/css/**", "/images/**", "/js/**", "/h2-console/**").permitAll()
               .requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll()
               .requestMatchers("/api/v1/**").hasRole(Role.USER.name())
               .anyRequest().permitAll()
               //TODO: redirect logout
               .and().logout().logoutSuccessUrl("/")
               .and().oauth2Login().userInfoEndpoint().userService(customOAuth2UserService);
       return http.build();
   }
}
