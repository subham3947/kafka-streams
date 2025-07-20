package com.kafka.kafka_streams.controller;

import com.kafka.kafka_streams.enums.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.kafka.kafka_streams.dto.OrderEvent;
import com.kafka.kafka_streams.producer.OrderEventProducer;

import java.util.UUID;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderEventProducer producer;

    @PostMapping
    public String createOrder() {
        String orderId = UUID.randomUUID().toString();
        OrderEvent event = new OrderEvent(orderId, OrderStatus.CREATED);
        producer.sendOrderEvent(event);
        return "Order CREATED: " + orderId;
    }
}

