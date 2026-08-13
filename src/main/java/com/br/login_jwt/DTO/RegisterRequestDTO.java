package com.br.login_jwt.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO para requisição de registro de novo usuário.
 */

@Getter
@Setter
public class RegisterRequestDTO {

    @NotBlank(message = "Usuário é obrigatório")
    @Size(min = 3, max = 50, message = "Usuário deve ter entre 3 e 50 caracteres")
    private String username;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, max = 100, message = "Senha deve ter entre 6 e 100 caracteres")
    private String password;
}
