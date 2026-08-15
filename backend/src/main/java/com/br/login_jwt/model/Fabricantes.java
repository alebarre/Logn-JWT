package com.br.login_jwt.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "fabricantes")
public class Fabricantes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String descricao;
}
