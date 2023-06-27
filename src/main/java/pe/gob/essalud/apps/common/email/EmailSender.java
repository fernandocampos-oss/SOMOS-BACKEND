package pe.gob.essalud.apps.common.email;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;

@Component
@RequiredArgsConstructor
public class EmailSender {

    private static final Logger logger = LoggerFactory.getLogger(EmailSender.class);
    @Value("${spring.mail.username}")
    private String emailFrom;
    private final JavaMailSender emailSender;

    public void sendSimple(String to, String from, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(emailFrom);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        message.setFrom(from);
        emailSender.send(message);

        logger.info("Correo enviado a: {}, con asunto: {}", to, subject);
    }

    @Value("classpath:static/images/essalud.png")
    Resource logoEsSalud;

    @Value("classpath:static/images/logo_white.png")
    Resource logoWhite;

    @SneakyThrows
    public void send(String to, String subject, String text) {

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper;
        helper = new MimeMessageHelper(message, true);
        helper.setFrom(emailFrom);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text, true);
        helper.addInline("essalud.png", logoEsSalud);
        helper.addInline("logo_white.png", logoWhite);

        emailSender.send(message);

        logger.info("Correo enviado a: {}, con asunto: {}", to, subject);

    }
}