package com.br.login_jwt.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

/**
 * Entidade de usuário com roles para autorização.
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
     * Conjunto de roles do usuário (ex: ROLE_USER, ROLE_ADMIN).
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private Set<String> roles;
}
