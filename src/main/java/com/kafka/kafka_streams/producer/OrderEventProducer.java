package com.kafka.kafka_streams.producer;

import com.kafka.kafka_streams.config.KafkaTopicsConfig;
import com.kafka.kafka_streams.dto.OrderEvent;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaTopicsConfig topics;

    public void sendOrderEvent(OrderEvent event) {
        kafkaTemplate.send(topics.getOrderEvents(), event.getOrderId(), event);
    }
}

