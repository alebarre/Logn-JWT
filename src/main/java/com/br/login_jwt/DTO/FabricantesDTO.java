package com.br.login_jwt.DTO;

import com.br.login_jwt.model.Fabricantes;
import lombok.Data;

/**
 * DTO de saída para fabricante.
 */
@Data
public class FabricantesDTO {
    private Long id;
    private String nome;
    private String descricao;

    public static FabricantesDTO from(Fabricantes fabricante) {
        FabricantesDTO dto = new FabricantesDTO();
        dto.setId(fabricante.getId());
        dto.setNome(fabricante.getNome());
        dto.setDescricao(fabricante.getDescricao());
        return dto;
    }
}
