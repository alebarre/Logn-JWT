package com.br.login_jwt.model;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Catálogo de roles (ex: ROLE_USER, ROLE_ADMIN).
 */
@Data
@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;
}
