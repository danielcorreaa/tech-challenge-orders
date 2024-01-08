package com.techchallenge.infrastructure.gateways;

import com.techchallenge.application.gateways.MessageGateway;
import com.techchallenge.domain.entity.Order;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Log4j2
@Component
public class MessageGatewayInteractor implements MessageGateway {

    private KafkaTemplate<String, Order> kafkaTemplate;
    @Value(value = "${kafka.topic.orders}")
    private String topic;

    public MessageGatewayInteractor(KafkaTemplate<String, Order> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void send(Order order) {
        try {
            CompletableFuture<SendResult<String, Order>> send = kafkaTemplate.send(topic, order);
        } catch (KafkaException ex) {
            log.error(ex.getMessage());
        }
    }
}
