package com.br.login_jwt.exception;

import com.br.login_jwt.DTO.ApiErrorDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Tratamento centralizado de exceções para retornar respostas de erro claras e consistentes,
 * evitando que falhas de negócio (credenciais inválidas, token expirado, etc.) sejam
 * mascaradas como erros genéricos (500) ou sem detalhes.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiErrorDTO> handleInvalidCredentials(InvalidCredentialsException ex,
                                                                 HttpServletRequest request) {
        log.warn("Falha de autenticação em {}: {}", request.getRequestURI(), ex.getMessage());
        return buildResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), request);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ApiErrorDTO> handleInvalidToken(InvalidTokenException ex,
                                                           HttpServletRequest request) {
        log.warn("Token inválido em {}: {}", request.getRequestURI(), ex.getMessage());
        return buildResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), request);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiErrorDTO> handleUsernameNotFound(UsernameNotFoundException ex,
                                                               HttpServletRequest request) {
        log.warn("Usuário não encontrado em {}: {}", request.getRequestURI(), ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorDTO> handleGeneric(Exception ex, HttpServletRequest request) {
        log.error("Erro não tratado em {}", request.getRequestURI(), ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "Erro interno inesperado. Consulte os logs do servidor para mais detalhes.", request);
    }

    private ResponseEntity<ApiErrorDTO> buildResponse(HttpStatus status, String message, HttpServletRequest request) {
        ApiErrorDTO body = new ApiErrorDTO(status.value(), status.getReasonPhrase(), message, request.getRequestURI());
        return ResponseEntity.status(status).body(body);
    }
}
