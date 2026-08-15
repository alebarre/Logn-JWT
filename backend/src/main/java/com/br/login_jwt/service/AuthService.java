package com.br.login_jwt.service;

import com.br.login_jwt.DTO.AuthResponseDTO;
import com.br.login_jwt.DTO.LoginRequestDTO;
import com.br.login_jwt.DTO.RegisterRequestDTO;
import com.br.login_jwt.exception.ExpiredVerificationCodeException;
import com.br.login_jwt.exception.InvalidCredentialsException;
import com.br.login_jwt.exception.InvalidTokenException;
import com.br.login_jwt.exception.InvalidVerificationCodeException;
import com.br.login_jwt.exception.UserAlreadyExistsException;
import com.br.login_jwt.model.PendingRegistration;
import com.br.login_jwt.model.Role;
import com.br.login_jwt.model.User;
import com.br.login_jwt.repository.PendingRegistrationRepository;
import com.br.login_jwt.repository.RoleRepository;
import com.br.login_jwt.repository.UserRepository;
import com.br.login_jwt.security.JwtService;
import com.br.login_jwt.util.VerificationCodeGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Set;

/**
 * Serviço de autenticação: registro em duas etapas (código por e-mail), login e refresh.
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PendingRegistrationRepository pendingRegistrationRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Value("${verification-code.expiration-seconds}")
    private long expirationSeconds;

    public AuthService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       PendingRegistrationRepository pendingRegistrationRepository,
                       JwtService jwtService,
                       PasswordEncoder passwordEncoder,
                       EmailService emailService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.pendingRegistrationRepository = pendingRegistrationRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    /**
     * Etapa 1 do registro: valida que o e-mail ainda não é usuário, gera o código de 5 dígitos,
     * guarda o cadastro pendente (senha já criptografada) e envia o código por e-mail.
     * O usuário só é criado após a confirmação do código.
     */
    @Transactional
    public void register(RegisterRequestDTO request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Usuário já cadastrado: " + request.getUsername());
        }

        String code = VerificationCodeGenerator.generate();

        // Um novo pedido de registro substitui o cadastro pendente anterior
        pendingRegistrationRepository.deleteByEmail(request.getUsername());

        PendingRegistration pending = new PendingRegistration();
        pending.setEmail(request.getUsername());
        pending.setPassword(passwordEncoder.encode(request.getPassword()));
        pending.setCode(code);
        pending.setExpiresAt(Instant.now().plusSeconds(expirationSeconds));
        pendingRegistrationRepository.save(pending);

        emailService.sendRegistrationCode(request.getUsername(), code, expirationSeconds);
    }

    /**
     * Etapa 2 do registro: confirma o código enviado por e-mail e, se válido e dentro
     * do prazo, cria o usuário e retorna os tokens.
     */
    @Transactional
    public AuthResponseDTO confirmRegister(String email, String code) {
        if (code == null || code.isBlank()) {
            throw new InvalidVerificationCodeException("Código vazio. Informe o código enviado para o seu e-mail.");
        }

        if (userRepository.existsByUsername(email)) {
            throw new UserAlreadyExistsException("Usuário já cadastrado: " + email);
        }

        PendingRegistration pending = pendingRegistrationRepository.findTopByEmailOrderByIdDesc(email)
                .orElseThrow(() -> new InvalidVerificationCodeException(
                        "Código inválido. Faça o cadastro para receber um novo código."));

        if (!pending.getCode().equals(code)) {
            throw new InvalidVerificationCodeException("Código inválido. Verifique o código enviado para o seu e-mail.");
        }

        if (pending.isExpired()) {
            pendingRegistrationRepository.deleteByEmail(email);
            throw new ExpiredVerificationCodeException("Código expirado. Faça o cadastro novamente para receber um novo código.");
        }

        User user = new User();
        user.setUsername(pending.getEmail());
        user.setPassword(pending.getPassword());
        user.setRole(findOrCreateRole("ROLE_USER"));
        userRepository.save(user);

        pendingRegistrationRepository.deleteByEmail(email);

        String access = jwtService.generateAccessToken(user.getUsername(), Set.of(user.getRole().getName()));
        String refresh = jwtService.generateRefreshToken(user.getUsername());

        return new AuthResponseDTO(access, refresh);
    }

    public AuthResponseDTO login(LoginRequestDTO request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + request.getUsername()));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Usuário ou senha inválidos.");
        }

        String access = jwtService.generateAccessToken(user.getUsername(), Set.of(user.getRole().getName()));
        String refresh = jwtService.generateRefreshToken(user.getUsername());

        return new AuthResponseDTO(access, refresh);
    }

    public AuthResponseDTO refresh(String refreshToken) {
        if (!jwtService.validateToken(refreshToken)) {
            throw new InvalidTokenException("Refresh token inválido ou expirado.");
        }

        String username = jwtService.extractUsername(refreshToken);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new InvalidTokenException("Usuário do token não existe mais."));
        String newAccess = jwtService.generateAccessToken(username, Set.of(user.getRole().getName()));
        String newRefresh = jwtService.generateRefreshToken(username);

        return new AuthResponseDTO(newAccess, newRefresh);
    }

    /**
     * Busca a role no catálogo; cria caso ainda não exista (ex: banco recém-criado).
     */
    private Role findOrCreateRole(String name) {
        return roleRepository.findByName(name).orElseGet(() -> {
            Role role = new Role();
            role.setName(name);
            return roleRepository.save(role);
        });
    }
}
