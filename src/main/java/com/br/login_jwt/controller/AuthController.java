package com.br.login_jwt.controller;

import com.br.login_jwt.DTO.AuthResponseDTO;
import com.br.login_jwt.DTO.LoginRequestDTO;
import com.br.login_jwt.DTO.RefreshRequestDTO;
import com.br.login_jwt.DTO.RegisterRequestDTO;
import com.br.login_jwt.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * Endpoints de autenticação: registro, login e refresh.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public AuthResponseDTO register(@Valid @RequestBody RegisterRequestDTO request) {
        return authService.register(request);
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
}
