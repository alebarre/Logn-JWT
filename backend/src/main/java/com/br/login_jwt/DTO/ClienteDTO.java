package com.br.login_jwt.DTO;

import com.br.login_jwt.model.Cliente;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO de saída para cliente, com a lista de endereços.
 */
@Data
public class ClienteDTO {
    private Long id;
    private String nome;
    private String sobrenome;
    private String telefone;
    private String email;
    private List<EnderecoDTO> enderecos;
    private LocalDateTime dataCadastro;

    public static ClienteDTO from(Cliente cliente) {
        ClienteDTO dto = new ClienteDTO();
        dto.setId(cliente.getId());
        dto.setNome(cliente.getNome());
        dto.setSobrenome(cliente.getSobrenome());
        dto.setTelefone(cliente.getTelefone());
        dto.setEmail(cliente.getEmail());
        dto.setEnderecos(cliente.getEnderecos().stream().map(EnderecoDTO::from).toList());
        dto.setDataCadastro(cliente.getDataCadastro());
        return dto;
    }
}
