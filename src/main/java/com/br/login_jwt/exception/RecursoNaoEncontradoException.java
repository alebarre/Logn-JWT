package com.br.login_jwt.exception;

/**
 * Lançada quando uma entidade referenciada por id (ex.: categoria, fabricante)
 * não existe no banco de dados.
 */
public class RecursoNaoEncontradoException extends RuntimeException {

    public RecursoNaoEncontradoException(String message) {
        super(message);
    }
}
