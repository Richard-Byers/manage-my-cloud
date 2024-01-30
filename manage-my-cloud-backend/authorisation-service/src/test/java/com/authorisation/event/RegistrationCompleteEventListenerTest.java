package com.authorisation.event;

import com.authorisation.services.UserService;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.util.Arrays;

import static com.authorisation.givens.RegistrationCompleteEventGivens.generateRegistrationCompleteEvent;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = RegistrationCompleteEventListener.class)
@ExtendWith(MockitoExtension.class)
class RegistrationCompleteEventListenerTest {


    @MockBean
    private Logger logger;
    @MockBean
    private UserService userService;
    @MockBean
    private JavaMailSender mailSender;
    @Autowired
    private RegistrationCompleteEventListener registrationCompleteEventListener;

    @Test
    void onApplicationEvent_sendsEmailToUser() throws Exception {
        //given
        RegistrationCompleteEvent registrationCompleteEvent = generateRegistrationCompleteEvent();
        Session session = Session.getDefaultInstance(System.getProperties());
        MimeMessage mimeMessage = new MimeMessage(session);
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);

        //when
        Mockito.doNothing().when(mailSender).send(any(MimeMessage.class));
        Mockito.doNothing().when(userService).saveVerificationToken(any(), any());
        given(mailSender.createMimeMessage()).willReturn(mimeMessage);

        registrationCompleteEventListener.onApplicationEvent(registrationCompleteEvent);

        //then
        String actualSender = Arrays.stream(mimeMessageHelper.getMimeMessage().getFrom()).map(Object::toString).findFirst().get();
        String actualUserEmail = Arrays.stream(mimeMessageHelper.getMimeMessage().getAllRecipients()).map(Object::toString).findFirst().get();

        verify(userService).saveVerificationToken(any(), any());
        verify(mailSender).createMimeMessage();
        assertEquals("Manage My Cloud <managemycloudverify@gmail.com>", actualSender);
        assertEquals(registrationCompleteEvent.getUserEntity().getEmail(), actualUserEmail);
    }

    @Test
    void onApplicationEvent_failsToSend_throwsException() {
        //given
        RegistrationCompleteEvent registrationCompleteEvent = generateRegistrationCompleteEvent();
        Session session = Session.getDefaultInstance(System.getProperties());
        MimeMessage mimeMessage = new MimeMessage(session);
        RuntimeException nestedRuntimeException = new RuntimeException("Error");

        //when
        Mockito.doNothing().when(userService).saveVerificationToken(any(), any());
        Mockito.doThrow(nestedRuntimeException).when(mailSender).send(any(MimeMessage.class));
        given(mailSender.createMimeMessage()).willReturn(mimeMessage);
        registrationCompleteEventListener.onApplicationEvent(registrationCompleteEvent);

        //then
        verify(userService).saveVerificationToken(any(), any());
        verify(mailSender).createMimeMessage();
        verify(logger).error("Error occurred while sending verification email to user: " + registrationCompleteEvent.getUserEntity().getEmail());
    }

    @Test
    void sendPasswordResetVerificationEmail_sendsEmailToUser() throws Exception {
        //given
        RegistrationCompleteEvent registrationCompleteEvent = generateRegistrationCompleteEvent();
        Session session = Session.getDefaultInstance(System.getProperties());
        MimeMessage mimeMessage = new MimeMessage(session);
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);

        //when
        Mockito.doNothing().when(mailSender).send(any(MimeMessage.class));
        given(mailSender.createMimeMessage()).willReturn(mimeMessage);

        registrationCompleteEventListener.sendPasswordResetVerificationEmail("url", registrationCompleteEvent.getUserEntity());

        //then
        String actualSender = Arrays.stream(mimeMessageHelper.getMimeMessage().getFrom()).map(Object::toString).findFirst().get();
        String actualUserEmail = Arrays.stream(mimeMessageHelper.getMimeMessage().getAllRecipients()).map(Object::toString).findFirst().get();

        verify(mailSender).createMimeMessage();
        assertEquals("Manage My Cloud <managemycloudverify@gmail.com>", actualSender);
        assertEquals(registrationCompleteEvent.getUserEntity().getEmail(), actualUserEmail);
    }

}
