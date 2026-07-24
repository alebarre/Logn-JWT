package com.br.login_jwt.DTO;

import lombok.Data;

@Data
public class ProdutoDTO {
    private Long id;
    private String nome;
    private String categoria;
    private String numSerie;
    private Double preco;
    private String dataCadastro;
}