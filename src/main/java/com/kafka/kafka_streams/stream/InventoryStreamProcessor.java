package com.kafka.kafka_streams.stream;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kafka.kafka_streams.config.KafkaTopicsConfig;
import com.kafka.kafka_streams.enums.InventoryStatus;
import com.kafka.kafka_streams.enums.OrderStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;


@Component
@Slf4j
@RequiredArgsConstructor
public class InventoryStreamProcessor {

    private final KafkaTopicsConfig topicsConfig;
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final String ORDER_ID = "orderId";
    private static final String STATUS = "status";

    @Bean
    public KStream<String, String> inventoryProcessor(StreamsBuilder builder) {
        log.info("💡 Kafka Streams inventoryProcessor started");
        KStream<String, String> orderStream = builder.stream(topicsConfig.getOrderEvents());

        KStream<String, String> inventoryEvents = orderStream
                .filter((key, value) -> {
                    try {
                        JsonNode node = mapper.readTree(value);
                        return node.has(STATUS) &&
                                OrderStatus.CREATED.name().equalsIgnoreCase(node.get(STATUS).asText());
                    } catch (Exception e) {
                        return false;
                    }
                })
                .mapValues(value -> {
                    try {
                        JsonNode order = mapper.readTree(value);
                        String orderId = order.get(ORDER_ID).asText();
                        boolean inStock = Math.random() > 0.2; //Logic to determine whether the item is in stock

                        ObjectNode event = mapper.createObjectNode();
                        event.put(ORDER_ID, orderId);
                        event.put(STATUS, inStock ? InventoryStatus.RESERVED.name()
                                : InventoryStatus.FAILED.name());

                        return event.toString();
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter((k, v) -> v != null);

        inventoryEvents.to(topicsConfig.getInventoryEvents());

        return inventoryEvents;
    }
}
