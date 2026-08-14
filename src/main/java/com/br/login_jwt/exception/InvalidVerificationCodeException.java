package com.br.login_jwt.exception;

/**
 * Lançada quando o código de verificação (cadastro ou recuperação de senha)
 * é inválido (não confere ou não existe) ou foi enviado vazio.
 */
public class InvalidVerificationCodeException extends RuntimeException {

    public InvalidVerificationCodeException(String message) {
        super(message);
    }
}
