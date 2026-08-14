package com.br.login_jwt.controller;

import com.br.login_jwt.DTO.AuthResponseDTO;
import com.br.login_jwt.DTO.ConfirmRegisterRequestDTO;
import com.br.login_jwt.DTO.ForgotPasswordRequestDTO;
import com.br.login_jwt.DTO.LoginRequestDTO;
import com.br.login_jwt.DTO.MessageResponseDTO;
import com.br.login_jwt.DTO.RefreshRequestDTO;
import com.br.login_jwt.DTO.RegisterRequestDTO;
import com.br.login_jwt.DTO.ResetPasswordRequestDTO;
import com.br.login_jwt.service.AuthService;
import com.br.login_jwt.service.PasswordResetService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * Endpoints de autenticação: registro, login, refresh e recuperação de senha.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final PasswordResetService passwordResetService;

    public AuthController(AuthService authService, PasswordResetService passwordResetService) {
        this.authService = authService;
        this.passwordResetService = passwordResetService;
    }

    /**
     * Etapa 1 do registro: envia o código de 5 dígitos para o e-mail informado.
     * O usuário só é criado após confirmar o código em /auth/register/confirm.
     */
    @PostMapping("/register")
    public MessageResponseDTO register(@Valid @RequestBody RegisterRequestDTO request) {
        authService.register(request);
        return new MessageResponseDTO("Código de confirmação enviado para o e-mail informado.");
    }

    /**
     * Etapa 2 do registro: confirma o código recebido por e-mail, cria o usuário e retorna os tokens.
     */
    @PostMapping("/register/confirm")
    public AuthResponseDTO confirmRegister(@Valid @RequestBody ConfirmRegisterRequestDTO request) {
        return authService.confirmRegister(request.getEmail(), request.getCode());
    }

    @PostMapping("/login")
    public AuthResponseDTO login(@Valid @RequestBody LoginRequestDTO request) {
        return authService.login(request);
    }

    /**
     * Endpoint para renovar o access token usando um refresh token válido.
     */
    @PostMapping("/refresh")
    public AuthResponseDTO refresh(@Valid @RequestBody RefreshRequestDTO request) {
        return authService.refresh(request.getRefreshToken());
    }

    /**
     * Passo 1 do "esqueci minha senha": gera o código de 5 dígitos e envia por e-mail.
     */
    @PostMapping("/forgot-password")
    public MessageResponseDTO forgotPassword(@Valid @RequestBody ForgotPasswordRequestDTO request) {
        passwordResetService.forgotPassword(request.getEmail());
        return new MessageResponseDTO("Código de recuperação enviado para o e-mail informado.");
    }

    /**
     * Passo 2: confirma o código recebido por e-mail e redefine a senha.
     */
    @PostMapping("/reset-password")
    public MessageResponseDTO resetPassword(@Valid @RequestBody ResetPasswordRequestDTO request) {
        passwordResetService.resetPassword(request.getEmail(), request.getCode(), request.getNewPassword());
        return new MessageResponseDTO("Senha redefinida com sucesso.");
    }
}
