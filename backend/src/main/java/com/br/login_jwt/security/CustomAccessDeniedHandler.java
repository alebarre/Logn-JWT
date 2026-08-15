package com.br.login_jwt.security;

import com.br.login_jwt.DTO.ApiErrorDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Acionado quando o usuário está autenticado, mas não possui a role necessária
 * para acessar o recurso (ex.: USER tentando executar uma ação restrita a ADMIN).
 * Retorna 403 com o motivo real da negação.
 */
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private static final Logger log = LoggerFactory.getLogger(CustomAccessDeniedHandler.class);

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Override
    public void handle(HttpServletRequest request,
                        HttpServletResponse response,
                        AccessDeniedException accessDeniedException) throws IOException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = (auth != null) ? auth.getName() : "desconhecido";
        String roles = (auth != null) ? auth.getAuthorities().toString() : "nenhuma";

        String message = String.format(
                "Usuário '%s' não possui permissão para %s %s. Roles atuais: %s",
                username, request.getMethod(), request.getRequestURI(), roles);

        log.warn("Acesso negado: {}", message);

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ApiErrorDTO body = new ApiErrorDTO(HttpStatus.FORBIDDEN.value(), "Forbidden", message, request.getRequestURI());
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
