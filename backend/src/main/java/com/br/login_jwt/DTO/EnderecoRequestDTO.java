package com.br.login_jwt.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO de entrada de endereço. No frontend, cep/logradouro/bairro/localidade/uf
 * são preenchidos pelo ViaCEP a partir do CEP; numero e complemento são digitados.
 */
@Data
public class EnderecoRequestDTO {

    @NotBlank(message = "CEP é obrigatório")
    @Pattern(regexp = "^\\d{5}-?\\d{3}$", message = "CEP deve estar no formato 00000-000 ou 00000000")
    private String cep;

    @NotBlank(message = "Logradouro é obrigatório")
    @Size(max = 150, message = "Logradouro deve ter no máximo 150 caracteres")
    private String logradouro;

    @Size(max = 20, message = "Número deve ter no máximo 20 caracteres")
    private String numero;

    @Size(max = 100, message = "Complemento deve ter no máximo 100 caracteres")
    private String complemento;

    @Size(max = 100, message = "Bairro deve ter no máximo 100 caracteres")
    private String bairro;

    @NotBlank(message = "Cidade (localidade) é obrigatória")
    @Size(max = 100, message = "Cidade deve ter no máximo 100 caracteres")
    private String localidade;

    @NotBlank(message = "UF é obrigatória")
    @Pattern(regexp = "^[A-Za-z]{2}$", message = "UF deve ter exatamente 2 letras")
    private String uf;
}
