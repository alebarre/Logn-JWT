package com.br.login_jwt.DTO;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO para requisição de registro de novo usuário.
 */

@Getter
@Setter
public class RegisterRequestDTO {

    private String username;
    private String password;

    // getters e setters
}
