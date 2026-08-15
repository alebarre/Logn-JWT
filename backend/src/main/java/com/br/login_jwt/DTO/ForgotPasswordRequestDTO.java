package com.br.login_jwt.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO para solicitação de recuperação de senha (envio do código por e-mail).
 */
@Getter
@Setter
public class ForgotPasswordRequestDTO {

    @NotBlank(message = "E-mail é obrigatório")
    @Email(message = "E-mail inválido")
    private String email;
}
