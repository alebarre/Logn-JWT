package com.br.login_jwt.exception;

/**
 * Lançada quando um token JWT (access ou refresh) é inválido, expirado ou malformado.
 */
public class InvalidTokenException extends RuntimeException {

    public InvalidTokenException(String message) {
        super(message);
    }
}
