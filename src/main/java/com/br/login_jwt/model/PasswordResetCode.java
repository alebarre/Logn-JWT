package com.br.login_jwt.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

/**
 * Código de recuperação de senha enviado por e-mail.
 * Cada e-mail mantém no máximo um código ativo; um novo pedido substitui o anterior.
 */
@Data
@Entity
@Table(name = "password_reset_codes")
public class PasswordResetCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false, length = 5)
    private String code;

    @Column(nullable = false)
    private Instant expiresAt;

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }
}
