package com.intellikart.order_service.config;
import com.intellikart.common_lib.constant.KafkaTopics;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic orderPlacedTopic() {
        return TopicBuilder.name(KafkaTopics.ORDER_PLACED_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }
}