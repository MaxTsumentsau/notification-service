package com.max2ba.notification_service.listener;

import com.max2ba.notification_service.annotation.Loggable;
import com.max2ba.notification_service.dto.SendEmailRequest;
import com.max2ba.notification_service.service.EmailService;
import io.github.resilience4j.retry.event.RetryOnRetryEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
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
             groupId = "${app.kafka.group-id}",
             concurrency = "3"
     )
     public void handle(SendEmailRequest request) {
          try {
               emailService.sendEmail(request);
          } catch (Exception e) {
               log.error("Ошибка обработки Kafka сообщения: {}", request, e);
          }
     }

     @EventListener
     public void onRetryEvent(RetryOnRetryEvent event) {
          log.warn("Попытка {} для {}", event.getNumberOfRetryAttempts(), event.getName());
     }
}
