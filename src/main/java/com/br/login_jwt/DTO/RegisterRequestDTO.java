package com.br.login_jwt.DTO;

import jakarta.validation.constraints.Email;
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

    /**
     * O username é o e-mail do usuário — é para ele que o código de recuperação de senha é enviado.
     */
    @NotBlank(message = "Usuário é obrigatório")
    @Email(message = "Usuário deve ser um e-mail válido")
    @Size(min = 3, max = 50, message = "Usuário deve ter entre 3 e 50 caracteres")
    private String username;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, max = 100, message = "Senha deve ter entre 6 e 100 caracteres")
    private String password;
}
