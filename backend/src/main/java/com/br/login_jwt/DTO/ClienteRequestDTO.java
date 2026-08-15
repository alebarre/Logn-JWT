package com.br.login_jwt.DTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * DTO de entrada para criação/atualização de cliente com seus endereços.
 */
@Data
public class ClienteRequestDTO {

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    @Pattern(regexp = ".*[A-Za-zÀ-ÿ].*", message = "Nome não pode conter apenas números")
    private String nome;

    @NotBlank(message = "Sobrenome é obrigatório")
    @Size(min = 2, max = 100, message = "Sobrenome deve ter entre 2 e 100 caracteres")
    @Pattern(regexp = ".*[A-Za-zÀ-ÿ].*", message = "Sobrenome não pode conter apenas números")
    private String sobrenome;

    @Pattern(regexp = "^[\\d\\s()+-]{8,20}$", message = "Telefone deve conter entre 8 e 20 caracteres (dígitos, espaços, parênteses, + ou -)")
    private String telefone;

    @NotBlank(message = "E-mail é obrigatório")
    @Email(message = "E-mail inválido")
    @Size(max = 150, message = "E-mail deve ter no máximo 150 caracteres")
    private String email;

    @NotEmpty(message = "Informe ao menos um endereço")
    @Valid
    private List<EnderecoRequestDTO> enderecos;
}
