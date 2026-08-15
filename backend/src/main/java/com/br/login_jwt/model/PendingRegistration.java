package com.br.login_jwt.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

/**
 * Cadastro pendente de confirmação: o usuário só é criado na tabela "users"
 * depois de confirmar o código de 5 dígitos enviado por e-mail.
 */
@Data
@Entity
@Table(name = "pending_registrations")
public class PendingRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * E-mail do usuário (que será o username após a confirmação).
     */
    @Column(nullable = false)
    private String email;

    /**
     * Senha já criptografada, aguardando a confirmação do código.
     */
    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 5)
    private String code;

    @Column(nullable = false)
    private Instant expiresAt;

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }
}
