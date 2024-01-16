package com.authorisation.event;

import com.authorisation.entities.UserEntity;
import com.authorisation.services.UserService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class RegistrationCompleteEventListener implements ApplicationListener<RegistrationCompleteEvent> {

    private final Logger logger;
    private final UserService userService;
    private final JavaMailSender mailSender;

    @Override
    public void onApplicationEvent(RegistrationCompleteEvent event) {

        //Get newly registered user
        UserEntity userEntity = event.getUserEntity();

        //Create verification token
        String verificationToken = UUID.randomUUID().toString();

        //Save verification token
        userService.saveVerificationToken(userEntity, verificationToken);

        //Build verification url to be sent to the user
        String verificationUrl = event.getApplicationUrl() + "/register/verifyEmail?token=" + verificationToken;

        //Send verification email to the user
        try {
            sendVerificationEmail(verificationUrl, userEntity);
        } catch (Exception e) {
            logger.error(String.format("Error occurred while sending verification email to user: %s", userEntity.getEmail()));
        }

    }

    public void sendVerificationEmail(String url, UserEntity user) throws MessagingException, UnsupportedEncodingException {

        String subject = "Please verify your email address";
        String sender = "Manage My Cloud";
        String mailContent = "<p> Hi, " + user.getFirstName() + ", </p>" +
                "<p>Thank you for registering with us," +
                " Please follow the link below to complete your registration.</p>" +
                "<a href=\"" + url + "\">Verify your email to activate your account</a>" +
                "<p> Thank you, <br> Manage My Cloud Registration Service </p>";
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(message);
        messageHelper.setFrom("managemycloudverify@gmail.com", sender);
        messageHelper.setTo(user.getEmail());
        messageHelper.setSubject(subject);
        messageHelper.setText(mailContent, true);
        mailSender.send(message);
    }

    public void sendPasswordResetVerificationEmail(String url, UserEntity user) throws MessagingException, UnsupportedEncodingException {

        String subject = "Password Reset";
        String sender = "Manage My Cloud";
        String mailContent = "<p> Hi, </p>" +
                "<p> Please follow the link below to reset your password.</p>" +
                "<a href=\"" + url + "\">Reset your password</a>" +
                "<p> Thank you, <br> Manage My Cloud Password Reset Service </p>";
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(message);
        messageHelper.setFrom("managemycloudverify@gmail.com", sender);
        messageHelper.setTo(user.getEmail());
        messageHelper.setSubject(subject);
        messageHelper.setText(mailContent, true);
        mailSender.send(message);

    }
}
