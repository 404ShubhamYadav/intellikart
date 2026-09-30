package com.intellikart.orchestration_service.config;

import com.intellikart.common_lib.constant.KafkaTopics;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic reserveStockCommandTopic() {
        return TopicBuilder.name(KafkaTopics.RESERVE_STOCK_COMMAND_TOPIC).partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic processPaymentCommandTopic() {
        return TopicBuilder.name(KafkaTopics.PROCESS_PAYMENT_COMMAND_TOPIC).partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic confirmOrderCommandTopic() {
        return TopicBuilder.name(KafkaTopics.CONFIRM_ORDER_COMMAND_TOPIC).partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic cancelOrderCommandTopic() {
        return TopicBuilder.name(KafkaTopics.CANCEL_ORDER_COMMAND_TOPIC).partitions(3).replicas(1).build();
    }
    @Bean
    public NewTopic releaseStockCommandTopic() {
        return TopicBuilder.name(KafkaTopics.RELEASE_STOCK_COMMAND_TOPIC).partitions(3).replicas(1).build();
    }
}
