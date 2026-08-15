package com.br.login_jwt.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO para confirmação do código de verificação enviado no cadastro.
 */
@Getter
@Setter
public class ConfirmRegisterRequestDTO {

    @NotBlank(message = "E-mail é obrigatório")
    @Email(message = "E-mail inválido")
    private String email;

    @NotBlank(message = "Código é obrigatório")
    @Pattern(regexp = "\\d{5}", message = "Código deve conter exatamente 5 dígitos")
    private String code;
}
