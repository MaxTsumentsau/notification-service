package com.max2ba.notification_service.config;

import lombok.AllArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@AllArgsConstructor
public class KafkaConfig {
     private static final String TOPIC_NAME = "${app.kafka.topic}";
     @Bean
     public NewTopic userEventsTopic() {
          return TopicBuilder.name(TOPIC_NAME)
                  .partitions(3)
                  .replicas(1)
                  .build();
     }
}
