package com.max2ba.notification_service.integration;

import com.max2ba.notification_service.dto.SendEmailRequest;
import com.max2ba.notification_service.dto.UserOperation;
import com.max2ba.notification_service.service.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;

import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.verify;

@ActiveProfiles("test")
@SpringBootTest
@Testcontainers
class KafkaIntegrationTest {
     private static final String TOPIC_NAME = "user-events";

     @Container
     static KafkaContainer kafka = new KafkaContainer("apache/kafka:3.7.0");

     @DynamicPropertySource
     static void kafkaProps(DynamicPropertyRegistry registry) {
          registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
     }
     @Autowired
     KafkaTemplate<String, SendEmailRequest> kafkaTemplate;

     @MockitoBean
     EmailService emailService;

     @Test
     void kafkaMessageIsConsumed() {
          SendEmailRequest event = new SendEmailRequest(UserOperation.CREATE, "cazzoculo@gmail.com");

          kafkaTemplate.send(TOPIC_NAME, event);

          await().atMost(5, TimeUnit.SECONDS)
                  .untilAsserted(() -> verify(emailService).sendEmail(event));
     }
}

