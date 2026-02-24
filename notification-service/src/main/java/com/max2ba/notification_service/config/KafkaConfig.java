package com.max2ba.notification_service.config;

import lombok.AllArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@AllArgsConstructor
public class KafkaConfig {

     @Bean
     public NewTopic userEventsTopic(@Value("${app.kafka.topic}") String topic) {
          return TopicBuilder.name(topic)
                  .partitions(3)
                  .replicas(1)
                  .build();
     }
}
