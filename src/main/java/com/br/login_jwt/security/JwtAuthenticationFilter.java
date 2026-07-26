package com.br.login_jwt.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro que intercepta requisições HTTP para validar o token JWT.
 *
 * Quando a autenticação falha, o motivo real (token ausente, expirado, malformado,
 * assinatura inválida ou usuário inexistente) é registrado no log e armazenado como
 * atributo da requisição ("jwt_error"), para que o {@link JwtAuthenticationEntryPoint}
 * possa devolver uma resposta 401 com uma mensagem clara em vez de um 403 genérico.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private static final String JWT_ERROR_ATTRIBUTE = "jwt_error";

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService,
                                   UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            request.setAttribute(JWT_ERROR_ATTRIBUTE,
                    "Header 'Authorization: Bearer <token>' não informado.");
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            String username = jwtService.extractUsername(token);
            var userDetails = userDetailsService.loadUserByUsername(username);

            var authToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authToken);
        } catch (ExpiredJwtException e) {
            log.warn("Token expirado em {}: {}", request.getRequestURI(), e.getMessage());
            request.setAttribute(JWT_ERROR_ATTRIBUTE, "Token expirado. Faça login novamente ou use o refresh token.");
        } catch (SignatureException e) {
            log.warn("Assinatura do token inválida em {}: {}", request.getRequestURI(), e.getMessage());
            request.setAttribute(JWT_ERROR_ATTRIBUTE, "Assinatura do token inválida.");
        } catch (MalformedJwtException e) {
            log.warn("Token malformado em {}: {}", request.getRequestURI(), e.getMessage());
            request.setAttribute(JWT_ERROR_ATTRIBUTE, "Token malformado.");
        } catch (UnsupportedJwtException e) {
            log.warn("Formato de token não suportado em {}: {}", request.getRequestURI(), e.getMessage());
            request.setAttribute(JWT_ERROR_ATTRIBUTE, "Formato de token não suportado.");
        } catch (UsernameNotFoundException e) {
            log.warn("Usuário do token não encontrado em {}: {}", request.getRequestURI(), e.getMessage());
            request.setAttribute(JWT_ERROR_ATTRIBUTE, "Usuário associado ao token não existe mais.");
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Falha ao validar token em {}: {}", request.getRequestURI(), e.getMessage());
            request.setAttribute(JWT_ERROR_ATTRIBUTE, "Token inválido.");
        }

        filterChain.doFilter(request, response);
    }
}

