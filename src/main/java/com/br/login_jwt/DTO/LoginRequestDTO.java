package com.br.login_jwt.DTO;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO para requisição de login de usuário.
 */

@Getter
@Setter
public class LoginRequestDTO {

    private String username;
    private String password;

    // getters e setters
}

