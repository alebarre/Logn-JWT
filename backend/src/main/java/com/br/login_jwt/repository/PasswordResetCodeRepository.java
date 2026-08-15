package com.br.login_jwt.repository;

import com.br.login_jwt.model.PasswordResetCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repositório JPA para códigos de recuperação de senha.
 */
public interface PasswordResetCodeRepository extends JpaRepository<PasswordResetCode, Long> {

    /**
     * Busca o código mais recente gerado para o e-mail.
     */
    Optional<PasswordResetCode> findTopByEmailOrderByIdDesc(String email);

    /**
     * Remove todos os códigos do e-mail (usado antes de gerar um novo e após o uso).
     */
    void deleteByEmail(String email);
}
