package com.br.login_jwt.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO de entrada para criação/atualização de produto.
 * Categoria e fabricante são referenciados por id; a existência de ambos
 * é verificada no service antes de persistir.
 */
@Data
public class ProdutoRequestDTO {

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, max = 150, message = "Nome deve ter entre 2 e 150 caracteres")
    @Pattern(regexp = ".*[A-Za-zÀ-ÿ].*", message = "Nome não pode conter apenas números")
    private String nome;

    @NotNull(message = "Categoria é obrigatória")
    private Long categoriaId;

    @NotBlank(message = "Número de série é obrigatório")
    @Size(max = 50, message = "Número de série deve ter no máximo 50 caracteres")
    @Pattern(regexp = "^[A-Za-z0-9-]+$", message = "Número de série deve conter apenas letras, números e hífen")
    private String numSerie;

    @NotNull(message = "Preço é obrigatório")
    @Positive(message = "Preço deve ser um valor numérico maior que zero")
    private Double preco;

    @NotNull(message = "Fabricante é obrigatório")
    private Long fabricanteId;
}
