package com.br.login_jwt.DTO;

import com.br.login_jwt.model.Categoria;
import lombok.Data;

/**
 * DTO de saída para categoria.
 */
@Data
public class CategoriaDTO {
    private Long id;
    private String nome;
    private String descricao;

    public static CategoriaDTO from(Categoria categoria) {
        CategoriaDTO dto = new CategoriaDTO();
        dto.setId(categoria.getId());
        dto.setNome(categoria.getNome());
        dto.setDescricao(categoria.getDescricao());
        return dto;
    }
}
