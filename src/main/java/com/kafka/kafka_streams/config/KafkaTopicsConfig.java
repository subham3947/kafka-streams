package com.kafka.kafka_streams.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "kafka.topics")
@Getter
@Setter
public class KafkaTopicsConfig {
    private String orderEvents;
    private String inventoryEvents;
    private String paymentEvents;
}
