package com.br.login_jwt.DTO;

import com.br.login_jwt.model.Endereco;
import lombok.Data;

/**
 * DTO de saída de endereço.
 */
@Data
public class EnderecoDTO {
    private Long id;
    private String cep;
    private String logradouro;
    private String numero;
    private String complemento;
    private String bairro;
    private String localidade;
    private String uf;

    public static EnderecoDTO from(Endereco endereco) {
        EnderecoDTO dto = new EnderecoDTO();
        dto.setId(endereco.getId());
        dto.setCep(endereco.getCep());
        dto.setLogradouro(endereco.getLogradouro());
        dto.setNumero(endereco.getNumero());
        dto.setComplemento(endereco.getComplemento());
        dto.setBairro(endereco.getBairro());
        dto.setLocalidade(endereco.getLocalidade());
        dto.setUf(endereco.getUf());
        return dto;
    }
}
