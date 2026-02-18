package com.max2ba.notification_service;

import com.max2ba.notification_service.dto.SendEmailRequest;
import com.max2ba.notification_service.dto.UserOperation;
import com.max2ba.notification_service.listener.UserEventListener;
import com.max2ba.notification_service.service.EmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserEventListenerTest {

     @Mock
     EmailService emailService;

     @InjectMocks
     UserEventListener listener;

     @Test
     void handleCreateEvent_callsSendAccountCreated() {
          SendEmailRequest request = new SendEmailRequest(UserOperation.CREATE, "bambaleila@gmail.com");

          listener.handle(request);

          verify(emailService, only()).sendEmail(request);
     }

     @Test
     void handleDeleteEvent_callsSendAccountDeleted() {
          SendEmailRequest request = new SendEmailRequest(UserOperation.DELETE, "bambaleila@gmail.com");

          listener.handle(request);

          verify(emailService, only()).sendEmail(request);
     }

     @Test
     void handleEvent_whenExceptionThrown() {
          SendEmailRequest request = new SendEmailRequest(null, "bambaleila@gmail.com");

          doThrow(new RuntimeException("Ошибка обработки Kafka сообщения:" + request))
                  .when(emailService)
                  .sendEmail(any(SendEmailRequest.class));

          listener.handle(request);

          verify(emailService, only()).sendEmail(request);
     }
}