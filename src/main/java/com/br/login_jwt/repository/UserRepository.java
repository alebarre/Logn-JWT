package com.br.login_jwt.repository;

import com.br.login_jwt.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repositório JPA para operações com usuários.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Busca usuário pelo username (que é o e-mail do usuário).
     */
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);
}

