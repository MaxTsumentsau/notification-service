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

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
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

     @Test
     void kafkaListenerProcessesMessagesInParallel() {
          SendEmailRequest event1 = new SendEmailRequest(UserOperation.CREATE, "a@gmail.com");
          SendEmailRequest event2 = new SendEmailRequest(UserOperation.CREATE, "b@gmail.com");
          SendEmailRequest event3 = new SendEmailRequest(UserOperation.CREATE, "c@gmail.com");

          long start = System.currentTimeMillis();

          kafkaTemplate.send(TOPIC_NAME, event1);
          kafkaTemplate.send(TOPIC_NAME, event2);
          kafkaTemplate.send(TOPIC_NAME, event3);

          await().atMost(10, TimeUnit.SECONDS)
                  .untilAsserted(() -> verify(emailService, times(3)).sendEmail(any()));

          long duration = System.currentTimeMillis() - start;

          assertThat(duration).isLessThan(3000);
     }

     @Test
     void kafkaListenerProcesses100MessagesInParallel() {
          int messageCount = 100;

          List<SendEmailRequest> events = IntStream.range(0, messageCount)
                  .mapToObj(i -> new SendEmailRequest(
                          UserOperation.CREATE,
                          "user" + i + "@gmail.com"
                  ))
                  .toList();

          long start = System.currentTimeMillis();

          events.forEach(event -> kafkaTemplate.send(TOPIC_NAME, event));

          await().atMost(20, TimeUnit.SECONDS)
                  .untilAsserted(() -> verify(emailService, times(messageCount)).sendEmail(any()));

          long duration = System.currentTimeMillis() - start;

          System.out.println("Обработка 100 сообщений заняла: " + duration + " ms");

          assertThat(duration).isLessThan(5000);
     }
}

