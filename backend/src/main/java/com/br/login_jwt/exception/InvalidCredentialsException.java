package com.br.login_jwt.exception;

/**
 * Lançada quando o usuário informa credenciais (usuário/senha) inválidas no login.
 */
public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException(String message) {
        super(message);
    }
}
