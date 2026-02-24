package com.max2ba.notification_service;

import com.max2ba.notification_service.dto.SendEmailRequest;
import com.max2ba.notification_service.dto.UserOperation;
import com.max2ba.notification_service.exception.EmailSendException;
import com.max2ba.notification_service.service.EmailServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {

     @Mock
     JavaMailSender mailSender;

     @InjectMocks
     EmailServiceImpl service;

     @BeforeEach
     void setup() {
          ReflectionTestUtils.setField(service, "from", "masik@gmail.com");
     }

     @Test
     void sendCreateEmail_sendsCorrectMessage() {
          var request = new SendEmailRequest(UserOperation.CREATE, "bambaleila@gmail.com");

          service.sendEmail(request);

          ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
          verify(mailSender).send(captor.capture());

          SimpleMailMessage msg = captor.getValue();

          assertThat(msg.getFrom()).isEqualTo("masik@gmail.com");
          assertThat(msg.getTo()[0]).isEqualTo("bambaleila@gmail.com");
          assertThat(msg.getSubject()).isEqualTo("Ваш аккаунт успешно создан");
          assertThat(msg.getText()).isEqualTo("Здравствуйте! Ваш аккаунт был успешно создан.");
     }

     @Test
     void sendDeleteEmail_sendsCorrectMessage() {
          var request = new SendEmailRequest(UserOperation.DELETE, "bambaleila@gmail.com");

          service.sendEmail(request);

          ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
          verify(mailSender).send(captor.capture());

          SimpleMailMessage msg = captor.getValue();
          assertThat(msg.getFrom()).isEqualTo("masik@gmail.com");
          assertThat(msg.getTo()[0]).isEqualTo("bambaleila@gmail.com");
          assertThat(msg.getSubject()).isEqualTo( "Ваш аккаунт был удалён");
          assertThat(msg.getText()).isEqualTo("Здравствуйте! Ваш аккаунт был удалён.");
     }

     @Test
     void send_throwsException_whenMailSenderFails() {
          var request = new SendEmailRequest(UserOperation.DELETE, "bambaleila@gmail.com");

          doThrow(new MailSendException("Ошибка отправки письма"))
                  .when(mailSender)
                  .send(any(SimpleMailMessage.class));

          assertThrows(EmailSendException.class, () -> service.sendEmail(request));
     }

     @Test
     void send_throwsException_whenEmailIsInvalid() {
          var request = new SendEmailRequest(UserOperation.CREATE, "");

          doThrow(new MailSendException("Ошибка отправки письма"))
                  .when(mailSender)
                  .send(any(SimpleMailMessage.class));

          assertThrows(EmailSendException.class, () -> service.sendEmail(request));
     }
}

