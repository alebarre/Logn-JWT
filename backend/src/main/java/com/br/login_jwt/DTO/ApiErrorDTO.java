package com.br.login_jwt.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;
import java.util.Map;

/**
 * Estrutura padrão de resposta de erro da API.
 * O campo "errors" só é preenchido em erros de validação, com o mapa
 * nome-do-campo -> mensagem, para o frontend exibir por campo do formulário.
 */
@Data
@AllArgsConstructor
public class ApiErrorDTO {

    private Instant timestamp;
    private int status;
    private String error;
    private String message;
    private String path;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<String, String> errors;

    public ApiErrorDTO(int status, String error, String message, String path) {
        this.timestamp = Instant.now();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    public ApiErrorDTO(int status, String error, String message, String path, Map<String, String> errors) {
        this(status, error, message, path);
        this.errors = errors;
    }
}
