package ru.vasyunin.springcloudrive.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import ru.vasyunin.springcloudrive.entity.RegistrationToken;
import ru.vasyunin.springcloudrive.entity.User;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Base64;

@Service
public class MailService {
    private final RegistrationTokenService tokenService;
    private final JavaMailSender sender;

    @Value("${cloudrive.web.server-url}")
    private String serverUrl;

    @Value("${spring.mail.username}")
    private String from;

    public MailService(RegistrationTokenService tokenService, JavaMailSender sender) {
        this.tokenService = tokenService;
        this.sender = sender;
    }

    public boolean sendRegistrationMessage(User user) {
        RegistrationToken token = tokenService.createRegistrationToken(user);

        String email_base64 = Base64.getUrlEncoder().encodeToString(user.getUsername().getBytes());

        //todo: executorService
        MimeMessage msg = sender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(msg, true);
            helper.setFrom(from);
            helper.setTo(user.getUsername());
            helper.setSubject("Please, activate your Cloudrive account");
            StringBuilder sb = new StringBuilder()
                    .append("<h1>Dear ").append(user.getFirstName()).append(" ").append(user.getLastName()).append("!</h1><br>")
                    .append("<div>Please confirm your email address by clicking the link below:</div>")
                    .append("<a href=\"").append(serverUrl).append("/register/confirm/").append(email_base64).append("/").append(token.getToken()).append("\">")
                    .append(serverUrl).append("/register/confirm/").append(email_base64).append("/").append(token.getToken()).append("</a>");

            helper.setText(sb.toString(), true);
            Thread thread = new Thread(() -> {
                sender.send(msg);
            });
            thread.start();
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        return false;
    }
}
