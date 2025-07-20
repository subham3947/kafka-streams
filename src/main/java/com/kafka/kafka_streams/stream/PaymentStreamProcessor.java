package com.kafka.kafka_streams.stream;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kafka.kafka_streams.config.KafkaTopicsConfig;
import com.kafka.kafka_streams.enums.InventoryStatus;
import com.kafka.kafka_streams.enums.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentStreamProcessor {


    private final KafkaTopicsConfig topicsConfig;
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final String ORDER_ID = "orderId";
    private static final String STATUS = "status";

    @Bean
    public KStream<String, String> paymentProcessor(StreamsBuilder builder) {
        KStream<String, String> inventoryStream = builder.stream(topicsConfig.getInventoryEvents());

        KStream<String, String> paymentEvents = inventoryStream
                .filter((key, value) -> {
                    try {
                        JsonNode node = mapper.readTree(value);
                        return node.has(STATUS) &&
                                InventoryStatus.RESERVED.name().equalsIgnoreCase(node.get(STATUS).asText());
                    } catch (Exception e) {
                        return false;
                    }
                })
                .mapValues(value -> {
                    try {
                        JsonNode inventoryEvent = mapper.readTree(value);
                        String orderId = inventoryEvent.get(ORDER_ID).asText();
                        boolean success = Math.random() > 0.1;

                        ObjectNode payment = mapper.createObjectNode();
                        payment.put(ORDER_ID, orderId);
                        payment.put(STATUS, success ? PaymentStatus.SUCCESS.name()
                                : PaymentStatus.FAILED.name());

                        return payment.toString();
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter((k, v) -> v != null);

        paymentEvents.to(topicsConfig.getPaymentEvents());

        return paymentEvents;
    }
}
