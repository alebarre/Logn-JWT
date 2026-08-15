package com.br.login_jwt.model;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Entidade de usuário com role para autorização.
 */
@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * O username é o e-mail do usuário (usado para login e recuperação de senha).
     */
    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    /**
     * Role do usuário (ex: ROLE_USER, ROLE_ADMIN). O FK role_id fica na tabela users.
     */
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;
}
