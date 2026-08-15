package com.br.login_jwt.model;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Endereço de um cliente. Os campos seguem a nomenclatura do ViaCEP
 * (logradouro, bairro, localidade, uf), que preenche o formulário no
 * frontend a partir do CEP digitado; numero e complemento são digitados.
 */
@Data
@Entity
@Table(name = "enderecos")
public class Endereco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 9)
    private String cep;

    @Column(nullable = false, length = 150)
    private String logradouro;

    @Column(length = 20)
    private String numero;

    @Column(length = 100)
    private String complemento;

    @Column(length = 100)
    private String bairro;

    @Column(nullable = false, length = 100)
    private String localidade;

    @Column(nullable = false, length = 2)
    private String uf;
}
