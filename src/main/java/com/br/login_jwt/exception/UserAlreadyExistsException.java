package com.br.login_jwt.exception;

/**
 * Lançada no registro quando o username ou e-mail já está cadastrado.
 */
public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
