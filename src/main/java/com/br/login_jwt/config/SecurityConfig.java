package com.br.login_jwt.config;

import com.br.login_jwt.security.CustomAccessDeniedHandler;
import com.br.login_jwt.security.JwtAuthenticationEntryPoint;
import com.br.login_jwt.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuração de segurança com autorização por roles.
 */
@Configuration
public class    SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
                          CustomAccessDeniedHandler customAccessDeniedHandler) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.customAccessDeniedHandler = customAccessDeniedHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**", "/v3/api-docs/**", "/swagger-ui/**").permitAll()

                        // USER e ADMIN podem listar
                        .requestMatchers(HttpMethod.GET, "/produtos/**", "/categorias/**")
                        .hasAnyRole("USER", "ADMIN")

                        // Apenas ADMIN pode criar, atualizar e deletar
                        .requestMatchers(HttpMethod.POST, "/produtos/**", "/categorias/**")
                        .hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/produtos/**", "/categorias/**")
                        .hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/produtos/**", "/categorias/**")
                        .hasRole("ADMIN")

                        .anyRequest().authenticated()
                )
                // Diferencia 401 (não autenticado: sem token/token inválido) de 403
                // (autenticado, mas sem a role necessária), cada um com uma mensagem clara.
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler)
                )
                .addFilterBefore(jwtAuthenticationFilter,
                        org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
