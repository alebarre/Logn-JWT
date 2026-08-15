package com.br.login_jwt.repository;

import com.br.login_jwt.model.PendingRegistration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repositório JPA para cadastros pendentes de confirmação por código.
 */
public interface PendingRegistrationRepository extends JpaRepository<PendingRegistration, Long> {

    /**
     * Busca o cadastro pendente mais recente do e-mail.
     */
    Optional<PendingRegistration> findTopByEmailOrderByIdDesc(String email);

    /**
     * Remove os cadastros pendentes do e-mail (antes de um novo pedido e após a confirmação).
     */
    void deleteByEmail(String email);
}
