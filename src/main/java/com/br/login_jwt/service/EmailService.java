package com.br.login_jwt.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Envio de e-mails transacionais (código de recuperação de senha).
 */
@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Envia o código de recuperação de senha para o e-mail do usuário.
     */
    public void sendPasswordResetCode(String to, String code, long expirationSeconds) {
        send(to, "Recuperação de senha - Código de verificação", """
                Olá,

                Seu código de recuperação de senha é: %s

                Este código expira em %d segundo(s). Se você não solicitou a recuperação, ignore este e-mail.
                """.formatted(code, expirationSeconds));
        log.info("Código de recuperação de senha enviado para {}", to);
    }

    /**
     * Envia o código de confirmação de cadastro para o e-mail do usuário.
     */
    public void sendRegistrationCode(String to, String code, long expirationSeconds) {
        send(to, "Confirmação de cadastro - Código de verificação", """
                Olá,

                Seu código de confirmação de cadastro é: %s

                Este código expira em %d segundo(s). Se você não solicitou este cadastro, ignore este e-mail.
                """.formatted(code, expirationSeconds));
        log.info("Código de confirmação de cadastro enviado para {}", to);
    }

    private void send(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
}
