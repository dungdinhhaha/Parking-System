package com.parking.system.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.parking.system.dto.response.ApiErrorResponse;
import lombok.RequiredArgsConstructor;
import java.io.IOException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final ObjectProvider<ObjectMapper> objectMapperProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> writeJsonError(request, response, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized", "Authentication required"))
                        .accessDeniedHandler((request, response, accessDeniedException) -> writeJsonError(request, response, HttpServletResponse.SC_FORBIDDEN, "Forbidden", "Access Denied")))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/auth-test.html").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/", "/*.html", "/css/**", "/js/**", "/images/**", "/webjars/**", "/favicon.ico").permitAll()
                        .anyRequest().authenticated())
                .authenticationProvider(authenticationProvider());
        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    private void writeJsonError(HttpServletRequest request, HttpServletResponse response, int status, String error, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        ObjectMapper objectMapper = objectMapperProvider.getIfAvailable(ObjectMapper::new);
        response.getWriter().write(objectMapper.writeValueAsString(ApiErrorResponse.builder()
                .timestamp(java.time.Instant.now())
                .status(status)
                .error(error)
                .message(message)
                .path(request.getRequestURI())
                .build()));
    }
}
