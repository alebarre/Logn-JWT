package com.br.login_jwt.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

/**
 * Estrutura padrão de resposta de erro da API.
 */
@Data
@AllArgsConstructor
public class ApiErrorDTO {

    private Instant timestamp;
    private int status;
    private String error;
    private String message;
    private String path;

    public ApiErrorDTO(int status, String error, String message, String path) {
        this.timestamp = Instant.now();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }
}
