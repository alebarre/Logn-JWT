package com.br.login_jwt.security;

import com.br.login_jwt.DTO.ApiErrorDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Acionado quando a requisição não está autenticada (sem token, token ausente,
 * inválido, malformado ou expirado). Retorna 401 com o motivo real da falha,
 * capturado previamente pelo {@link JwtAuthenticationFilter}.
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationEntryPoint.class);
    private static final String JWT_ERROR_ATTRIBUTE = "jwt_error";

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Override
    public void commence(HttpServletRequest request,
                          HttpServletResponse response,
                          AuthenticationException authException) throws IOException {

        String reason = (String) request.getAttribute(JWT_ERROR_ATTRIBUTE);
        if (reason == null) {
            reason = "Requisição não autenticada: informe um token Bearer válido no header Authorization.";
        }

        log.warn("Acesso não autenticado a {}: {}", request.getRequestURI(), reason);

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ApiErrorDTO body = new ApiErrorDTO(HttpStatus.UNAUTHORIZED.value(), "Unauthorized", reason, request.getRequestURI());
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
