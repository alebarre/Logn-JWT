package com.br.login_jwt.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.MailPreparationException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

/**
 * Envio de e-mails transacionais (código de confirmação de cadastro e de
 * recuperação de senha), com corpo em HTML e fallback em texto puro.
 */
@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    private final String registrationTemplate;
    private final String passwordResetTemplate;

    @Value("${spring.mail.username}")
    private String from;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
        this.registrationTemplate = loadTemplate("templates/email/registration-code.html");
        this.passwordResetTemplate = loadTemplate("templates/email/password-reset-code.html");
    }

    /**
     * Envia o código de recuperação de senha para o e-mail do usuário.
     */
    public void sendPasswordResetCode(String to, String code, long expirationSeconds) {
        String expiration = formatExpiration(expirationSeconds);
        String plainText = """
                Olá,

                Seu código de recuperação de senha é: %s

                Este código expira em %s. Se você não solicitou a recuperação, ignore este e-mail.
                """.formatted(code, expiration);

        send(to, "Recuperação de senha - Código de verificação",
                plainText, fillTemplate(passwordResetTemplate, code, expiration));
        log.info("Código de recuperação de senha enviado para {}", to);
    }

    /**
     * Envia o código de confirmação de cadastro para o e-mail do usuário.
     */
    public void sendRegistrationCode(String to, String code, long expirationSeconds) {
        String expiration = formatExpiration(expirationSeconds);
        String plainText = """
                Olá,

                Seu código de confirmação de cadastro é: %s

                Este código expira em %s. Se você não solicitou este cadastro, ignore este e-mail.
                """.formatted(code, expiration);

        send(to, "Confirmação de cadastro - Código de verificação",
                plainText, fillTemplate(registrationTemplate, code, expiration));
        log.info("Código de confirmação de cadastro enviado para {}", to);
    }

    private void send(String to, String subject, String plainText, String html) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(plainText, html);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new MailPreparationException("Falha ao montar o e-mail para " + to, e);
        }
    }

    private static String fillTemplate(String template, String code, String expiration) {
        return template
                .replace("{{code}}", code)
                .replace("{{expiration}}", expiration);
    }

    private static String formatExpiration(long seconds) {
        if (seconds >= 60 && seconds % 60 == 0) {
            long minutes = seconds / 60;
            return minutes == 1 ? "1 minuto" : minutes + " minutos";
        }
        return seconds == 1 ? "1 segundo" : seconds + " segundos";
    }

    private static String loadTemplate(String path) {
        try {
            return new ClassPathResource(path).getContentAsString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("Não foi possível carregar o template de e-mail: " + path, e);
        }
    }
}
