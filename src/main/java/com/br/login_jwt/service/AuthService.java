package com.br.login_jwt.service;

import com.br.login_jwt.DTO.AuthResponseDTO;
import com.br.login_jwt.DTO.LoginRequestDTO;
import com.br.login_jwt.DTO.RegisterRequestDTO;
import com.br.login_jwt.exception.InvalidCredentialsException;
import com.br.login_jwt.exception.InvalidTokenException;
import com.br.login_jwt.model.User;
import com.br.login_jwt.repository.UserRepository;
import com.br.login_jwt.security.JwtService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Serviço de autenticação: registro, login e refresh.
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository,
                       JwtService jwtService,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponseDTO register(RegisterRequestDTO request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(Set.of("ROLE_USER"));

        userRepository.save(user);

        String access = jwtService.generateAccessToken(user.getUsername());
        String refresh = jwtService.generateRefreshToken(user.getUsername());

        return new AuthResponseDTO(access, refresh);
    }

    public AuthResponseDTO login(LoginRequestDTO request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + request.getUsername()));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Usuário ou senha inválidos.");
        }

        String access = jwtService.generateAccessToken(user.getUsername());
        String refresh = jwtService.generateRefreshToken(user.getUsername());

        return new AuthResponseDTO(access, refresh);
    }

    public AuthResponseDTO refresh(String refreshToken) {
        if (!jwtService.validateToken(refreshToken)) {
            throw new InvalidTokenException("Refresh token inválido ou expirado.");
        }

        String username = jwtService.extractUsername(refreshToken);
        String newAccess = jwtService.generateAccessToken(username);
        String newRefresh = jwtService.generateRefreshToken(username);

        return new AuthResponseDTO(newAccess, newRefresh);
    }
}
