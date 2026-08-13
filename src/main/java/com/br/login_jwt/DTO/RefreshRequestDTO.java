package com.br.login_jwt.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO para requisição de renovação de access token.
 */
@Getter
@Setter
public class RefreshRequestDTO {

    @NotBlank(message = "Refresh token é obrigatório")
    private String refreshToken;
}
