package com.br.login_jwt.service;

import com.br.login_jwt.exception.ExpiredVerificationCodeException;
import com.br.login_jwt.exception.InvalidVerificationCodeException;
import com.br.login_jwt.model.PasswordResetCode;
import com.br.login_jwt.model.User;
import com.br.login_jwt.repository.PasswordResetCodeRepository;
import com.br.login_jwt.repository.UserRepository;
import com.br.login_jwt.util.VerificationCodeGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Fluxo de "esqueci minha senha":
 * 1) forgotPassword: gera um código de 5 dígitos, salva com validade e envia por e-mail;
 * 2) resetPassword: confirma o código recebido e define a nova senha.
 */
@Service
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetCodeRepository codeRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Value("${verification-code.expiration-seconds}")
    private long expirationSeconds;

    public PasswordResetService(UserRepository userRepository,
                                PasswordResetCodeRepository codeRepository,
                                EmailService emailService,
                                PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.codeRepository = codeRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Gera e envia o código de recuperação. Um novo pedido invalida o código anterior.
     * O e-mail informado é o próprio username do usuário.
     */
    @Transactional
    public void forgotPassword(String email) {
        userRepository.findByUsername(email)
                .orElseThrow(() -> new UsernameNotFoundException("E-mail não cadastrado: " + email));

        String code = VerificationCodeGenerator.generate();

        codeRepository.deleteByEmail(email);

        PasswordResetCode resetCode = new PasswordResetCode();
        resetCode.setEmail(email);
        resetCode.setCode(code);
        resetCode.setExpiresAt(Instant.now().plusSeconds(expirationSeconds));
        codeRepository.save(resetCode);

        emailService.sendPasswordResetCode(email, code, expirationSeconds);
    }

    /**
     * Valida o código e, se válido e dentro do prazo, redefine a senha do usuário.
     */
    @Transactional
    public void resetPassword(String email, String code, String newPassword) {
        if (code == null || code.isBlank()) {
            throw new InvalidVerificationCodeException("Código vazio. Informe o código enviado para o seu e-mail.");
        }

        User user = userRepository.findByUsername(email)
                .orElseThrow(() -> new UsernameNotFoundException("E-mail não cadastrado: " + email));

        PasswordResetCode resetCode = codeRepository.findTopByEmailOrderByIdDesc(email)
                .orElseThrow(() -> new InvalidVerificationCodeException(
                        "Código inválido. Solicite um novo código de recuperação."));

        if (!resetCode.getCode().equals(code)) {
            throw new InvalidVerificationCodeException("Código inválido. Verifique o código enviado para o seu e-mail.");
        }

        if (resetCode.isExpired()) {
            codeRepository.deleteByEmail(email);
            throw new ExpiredVerificationCodeException("Código expirado. Solicite um novo código de recuperação.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Código é de uso único: descarta após a troca de senha
        codeRepository.deleteByEmail(email);
    }
}
