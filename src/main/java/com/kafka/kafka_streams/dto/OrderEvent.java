package com.kafka.kafka_streams.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.kafka.kafka_streams.enums.OrderStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderEvent {
    private String orderId;
    private OrderStatus status;
}
