package com.br.login_jwt.exception;

/**
 * Lançada quando uma operação tentaria duplicar um recurso que deve ser
 * único (ex.: cliente com e-mail já cadastrado). Tratada como 409 Conflict.
 */
public class RecursoJaExisteException extends RuntimeException {
    public RecursoJaExisteException(String message) {
        super(message);
    }
}
