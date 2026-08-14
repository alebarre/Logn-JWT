package com.br.login_jwt.exception;

/**
 * Lançada quando o código de verificação (cadastro ou recuperação de senha)
 * existe mas já passou do prazo de validade.
 */
public class ExpiredVerificationCodeException extends RuntimeException {

    public ExpiredVerificationCodeException(String message) {
        super(message);
    }
}
