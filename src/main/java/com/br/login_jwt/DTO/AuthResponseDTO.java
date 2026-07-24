package com.br.login_jwt.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Resposta de autenticação contendo access e refresh tokens.
 */
@Data
@AllArgsConstructor
public class AuthResponseDTO {

    private String accessToken;
    private String refreshToken;
}
