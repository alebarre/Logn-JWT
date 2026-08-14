package com.br.login_jwt.exception;

import com.br.login_jwt.DTO.ApiErrorDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mail.MailException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

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

    @ExceptionHandler(InvalidVerificationCodeException.class)
    public ResponseEntity<ApiErrorDTO> handleInvalidVerificationCode(InvalidVerificationCodeException ex,
                                                                      HttpServletRequest request) {
        log.warn("Código de verificação inválido em {}: {}", request.getRequestURI(), ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(ExpiredVerificationCodeException.class)
    public ResponseEntity<ApiErrorDTO> handleExpiredVerificationCode(ExpiredVerificationCodeException ex,
                                                                      HttpServletRequest request) {
        log.warn("Código de verificação expirado em {}: {}", request.getRequestURI(), ex.getMessage());
        return buildResponse(HttpStatus.GONE, ex.getMessage(), request);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiErrorDTO> handleUserAlreadyExists(UserAlreadyExistsException ex,
                                                                HttpServletRequest request) {
        log.warn("Conflito de cadastro em {}: {}", request.getRequestURI(), ex.getMessage());
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    @ExceptionHandler(MailException.class)
    public ResponseEntity<ApiErrorDTO> handleMailFailure(MailException ex, HttpServletRequest request) {
        log.error("Falha ao enviar e-mail em {}", request.getRequestURI(), ex);
        return buildResponse(HttpStatus.SERVICE_UNAVAILABLE,
                "Não foi possível enviar o e-mail com o código. Tente novamente em instantes.", request);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiErrorDTO> handleUsernameNotFound(UsernameNotFoundException ex,
                                                               HttpServletRequest request) {
        log.warn("Usuário não encontrado em {}: {}", request.getRequestURI(), ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ApiErrorDTO> handleRecursoNaoEncontrado(RecursoNaoEncontradoException ex,
                                                                   HttpServletRequest request) {
        log.warn("Recurso referenciado não encontrado em {}: {}", request.getRequestURI(), ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorDTO> handleValidation(MethodArgumentNotValidException ex,
                                                          HttpServletRequest request) {
        Map<String, String> errors = new LinkedHashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        log.warn("Falha de validação em {}: {}", request.getRequestURI(), errors);
        ApiErrorDTO body = new ApiErrorDTO(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Erro de validação nos campos enviados.", request.getRequestURI(), errors);
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorDTO> handleMalformedJson(HttpMessageNotReadableException ex,
                                                             HttpServletRequest request) {
        log.warn("Corpo da requisição inválido em {}: {}", request.getRequestURI(), ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST,
                "Corpo da requisição inválido. Verifique se os campos foram enviados no formato esperado.",
                request);
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
