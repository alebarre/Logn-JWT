package com.br.login_jwt.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Resposta simples com uma mensagem informativa.
 */
@Data
@AllArgsConstructor
public class MessageResponseDTO {

    private String message;
}
