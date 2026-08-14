package com.br.login_jwt.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "clientes")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, length = 100)
    private String sobrenome;

    @Column(length = 20)
    private String telefone;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    /**
     * Relação unidirecional 1 cliente -> N endereços: os endereços vivem
     * apenas dentro do cliente (cascade + orphanRemoval), então atualizar
     * a lista substitui os registros antigos.
     */
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "cliente_id", nullable = false)
    private List<Endereco> enderecos = new ArrayList<>();

    private LocalDateTime dataCadastro;
}
