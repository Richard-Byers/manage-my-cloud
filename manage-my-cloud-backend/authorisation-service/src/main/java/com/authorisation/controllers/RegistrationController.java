package com.authorisation.controllers;

import com.authorisation.event.RegistrationCompleteEvent;
import com.authorisation.event.RegistrationCompleteEventListener;
import com.authorisation.registration.RegistrationRequest;
import com.authorisation.registration.password.PasswordResetRequest;
import com.authorisation.entities.VerificationToken;
import com.authorisation.repositories.VerificationTokenRepository;
import com.authorisation.entities.UserEntity;
import com.authorisation.services.UserService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/register")
public class RegistrationController {

    private final UserService userService;

    private final ApplicationEventPublisher eventPublisher;

    private final VerificationTokenRepository verificationTokenRepository;

    private final RegistrationCompleteEventListener eventListener;

    private final HttpServletRequest request;

    @PostMapping
    public String registerUser(@RequestBody RegistrationRequest registrationRequest, final HttpServletRequest request) {

        //Call register user method to register a user
        UserEntity userEntity = userService.registerUser(registrationRequest);

        //Publish event for email sending
        eventPublisher.publishEvent(new RegistrationCompleteEvent(userEntity, applicationUrl(request)));

        //Return the email of the user to use in the confirmation message
        return String.format("Confirmation email has been sent to %s. Please verify your account", userEntity.getEmail());
    }

    @GetMapping("verifyEmail")
    public String verifyEmail(@RequestParam("token") String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token);

        if (verificationToken.getUserEntity().isEnabled()) {
            return "this account has already been verified, please login";
        }

        String verificationResult = userService.validateToken(token);

        if (verificationResult.equals("valid")) {
            return "Email has been verified successfully. You can now login";
        } else {
            String resendUrl = applicationUrl(request) + "/register/resendVerificationEmail?token=" + token;
            return "The link is invalid or broken, <a href=\"" + resendUrl + "\">Click Here</a> to resend verification email";
        }
    }

    @GetMapping("/resendVerificationEmail")
    public String resendVerificationToken(@RequestParam("token") String oldToken, HttpServletRequest request) {

        VerificationToken verificationToken = userService.generateNewVerificationToken(oldToken);

        UserEntity userEntity = verificationToken.getUserEntity();
        resendVerificationEmail(userEntity, applicationUrl(request), verificationToken);

        return String.format("A new verification email has been sent to %s. Please verify your account", userEntity.getEmail());
    }

    @PostMapping("/resetUserPassword")
    public String resetPasswordRequest(@RequestBody PasswordResetRequest passwordResetRequest, final HttpServletRequest request) {

        Optional<UserEntity> userEntity = userService.findUserByEmail(passwordResetRequest.getEmail());

        if (userEntity.isPresent()) {
            String passwordResetToken = UUID.randomUUID().toString();
            userService.createPasswordResetToken(userEntity.get(), passwordResetToken);
            sendPasswordResetEmailLink(userEntity.get(), applicationUrl(request), passwordResetToken);
            return String.format("A new password reset link has been sent to %s. Please check your email", userEntity.get().getEmail());
        } else {
            return "No user found with email " + passwordResetRequest.getEmail();
        }
    }

    @PostMapping("/resetPassword")
    public String resetPassword(@RequestBody PasswordResetRequest passwordResetRequest, @RequestParam("passwordResetToken") String passwordResetToken) {
        String tokenValidationResult = userService.validatePasswordResetToken(passwordResetToken);

        if (!tokenValidationResult.equals("valid")) {
            return "Invalid password reset token";
        } else {
            UserEntity userEntity = userService.findUserByPasswordToken(passwordResetToken);

            if (userEntity != null) {
                userService.resetUserPassword(userEntity, passwordResetRequest.getNewPassword());
                return "Password reset successfully";
            } else {
                return "Invalid password reset token";
            }
        }
    }

    private void resendVerificationEmail(UserEntity userEntity, String applicationUrl, VerificationToken verificationToken) {
        String verificationUrl = applicationUrl + "/register/verifyEmail?token=" + verificationToken.getToken();

        try {
            eventListener.sendVerificationEmail(verificationUrl, userEntity);
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("Error occurred while sending verification email to user: " + userEntity.getEmail());
        }
    }

    public String applicationUrl(HttpServletRequest request) {
        return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }

    private void sendPasswordResetEmailLink(UserEntity userEntity, String applicationUrl, String passwordResetToken) {
        String verificationUrl = applicationUrl + "/register/resetPassword?token=" + passwordResetToken;

        try {
            eventListener.sendPasswordResetVerificationEmail(verificationUrl, userEntity);
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("Error occurred while sending password reset email to user: " + userEntity.getEmail());
        }
    }

}
