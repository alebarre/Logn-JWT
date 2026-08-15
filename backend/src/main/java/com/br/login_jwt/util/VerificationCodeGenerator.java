package com.br.login_jwt.util;

import java.security.SecureRandom;

/**
 * Geração de códigos numéricos de verificação (cadastro e recuperação de senha).
 */
public final class VerificationCodeGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();

    private VerificationCodeGenerator() {
    }

    /**
     * Gera um código de exatamente 5 dígitos (com zeros à esquerda quando necessário).
     */
    public static String generate() {
        return String.format("%05d", RANDOM.nextInt(100_000));
    }
}
