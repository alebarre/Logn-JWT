package com.br.login_jwt.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "produtos")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String categoria;
    private String numSerie;
    private Double preco;
    @ManyToOne
    @JoinColumn(name = "fabricante_id")
    private Fabricantes fabricantes;
    private LocalDateTime dataCadastro;
}
