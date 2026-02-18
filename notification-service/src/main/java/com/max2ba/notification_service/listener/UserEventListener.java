package com.max2ba.notification_service.listener;

import com.max2ba.notification_service.annotation.Loggable;
import com.max2ba.notification_service.dto.SendEmailRequest;
import com.max2ba.notification_service.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Loggable
@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventListener {
     private final EmailService emailService;

     @KafkaListener(
             topics = "${app.kafka.topic}",
             groupId = "${app.kafka.group-id}"
     )
     public void handle(SendEmailRequest request) {
          try {
               emailService.sendEmail(request);
          } catch (Exception e) {
               log.error("Ошибка обработки Kafka сообщения: {}", request, e);
          }
     }
}
