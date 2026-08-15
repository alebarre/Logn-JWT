package com.br.login_jwt.DTO;

import com.br.login_jwt.model.Produto;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO de saída para produto, com categoria e fabricante já resolvidos.
 */
@Data
public class ProdutoDTO {
    private Long id;
    private String nome;
    private CategoriaDTO categoria;
    private String numSerie;
    private Double preco;
    private FabricantesDTO fabricantes;
    private LocalDateTime dataCadastro;

    public static ProdutoDTO from(Produto produto) {
        ProdutoDTO dto = new ProdutoDTO();
        dto.setId(produto.getId());
        dto.setNome(produto.getNome());
        dto.setCategoria(CategoriaDTO.from(produto.getCategoria()));
        dto.setNumSerie(produto.getNumSerie());
        dto.setPreco(produto.getPreco());
        dto.setFabricantes(FabricantesDTO.from(produto.getFabricantes()));
        dto.setDataCadastro(produto.getDataCadastro());
        return dto;
    }
}