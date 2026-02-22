package com.max2ba.notification_service;

import com.max2ba.notification_service.dto.SendEmailRequest;
import com.max2ba.notification_service.dto.UserOperation;
import com.max2ba.notification_service.entity.EmailDlq;
import com.max2ba.notification_service.repository.EmailDlqRepository;
import com.max2ba.notification_service.service.EmailDlqService;
import com.max2ba.notification_service.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class EmailDlqServiceTest {

     private EmailDlqRepository repository;
     private EmailService emailService;
     private EmailDlqService dlqService;

     @BeforeEach
     void setup() {
          repository = mock(EmailDlqRepository.class);
          emailService = mock(EmailService.class);
          dlqService = new EmailDlqService(repository, emailService);
     }

     @Test
     void saveToDlq_shouldSave() {
          EmailDlq dlq = new EmailDlq();
          dlqService.saveToDlq(dlq);

          verify(repository, times(1)).save(dlq);
     }

     @Test
     void processDlq_shouldDeleteOnSuccess() {
          EmailDlq entry = new EmailDlq(
                  UUID.randomUUID(),
                  "noTimeToDie@gmail.com",
                  "subject",
                  "text",
                  UserOperation.CREATE,
                  0,
                  null
          );

          when(repository.findAll()).thenReturn(List.of(entry));

          dlqService.processDlq();

          verify(emailService, times(1))
                  .sendEmail(any(SendEmailRequest.class));

          verify(repository, times(1)).delete(entry);
          verify(repository, never()).save(entry);
     }

     @Test
     void processDlq_shouldIncreaseWhenFailure() {
          EmailDlq entry = new EmailDlq(
                  UUID.randomUUID(),
                  "noTimeToDie@gmail.com",
                  "subject",
                  "text",
                  UserOperation.CREATE,
                  0,
                  null
          );

          when(repository.findAll()).thenReturn(List.of(entry));
          doThrow(new RuntimeException("SMTP error"))
                  .when(emailService)
                  .sendEmail(any(SendEmailRequest.class));

          dlqService.processDlq();

          assertThat(entry.getAttempts()).isEqualTo(1);
          verify(repository, times(1)).save(entry);
          verify(repository, never()).delete(entry);
     }

     @Test
     void processDlq_shouldSkipWithFiveAttempts() {
          EmailDlq entry = new EmailDlq(
                  UUID.randomUUID(),
                  "noTimeToDie@gmail.com",
                  "subject",
                  "text",
                  UserOperation.CREATE,
                  5,
                  null
          );

          when(repository.findAll()).thenReturn(List.of(entry));

          dlqService.processDlq();

          verify(emailService, never()).sendEmail(any());
          verify(repository, never()).delete(any());
          verify(repository, never()).save(any());
     }
}
