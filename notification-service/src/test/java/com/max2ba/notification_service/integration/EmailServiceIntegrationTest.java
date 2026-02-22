package com.max2ba.notification_service.integration;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetupTest;
import com.max2ba.notification_service.dto.SendEmailRequest;
import com.max2ba.notification_service.dto.UserOperation;
import com.max2ba.notification_service.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
class EmailServiceIntegrationTest {

     private GreenMail greenMail;
     @MockitoBean
     KafkaTemplate<String, SendEmailRequest> kafkaTemplate;
     @MockitoBean
     KafkaAdmin kafkaAdmin;

     @BeforeEach
     void startMailServer() {
          greenMail = new GreenMail(ServerSetupTest.SMTP);
          greenMail.start();
     }

     @AfterEach
     void stopMailServer() {
          greenMail.stop();
     }

     @Autowired
     EmailService emailService;

     @Test
     void sendEmail_sendCreateMessage() throws MessagingException, IOException {
          var request = new SendEmailRequest(UserOperation.CREATE, "max@gmail.com");

          emailService.sendEmail(request);

          boolean received = greenMail.waitForIncomingEmail(5000, 1);
          assertThat(received).isTrue();
          MimeMessage[] messages = greenMail.getReceivedMessages();
          assertThat(messages.length).isEqualTo(1);
          assertThat(messages[0].getSubject()).isEqualTo("Ваш аккаунт успешно создан");
          assertThat(messages[0].getContent()).isEqualTo("Здравствуйте! Ваш аккаунт был успешно создан.");
     }

     @Test
     void sendEmail_sendDeleteMessage() throws MessagingException, IOException {
          var request = new SendEmailRequest(UserOperation.DELETE, "max@gmail.com");

          emailService.sendEmail(request);

          boolean received = greenMail.waitForIncomingEmail(5000, 1);
          assertThat(received).isTrue();
          MimeMessage[] messages = greenMail.getReceivedMessages();
          assertThat(messages.length).isEqualTo(1);
          assertThat(messages[0].getSubject()).isEqualTo("Ваш аккаунт был удалён");
          assertThat(messages[0].getContent()).isEqualTo("Здравствуйте! Ваш аккаунт был удалён.");
     }
}