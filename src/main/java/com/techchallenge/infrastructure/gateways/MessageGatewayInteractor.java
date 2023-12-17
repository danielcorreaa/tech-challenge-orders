package com.techchallenge.infrastructure.gateways;

import com.techchallenge.application.gateways.MessageGateway;
import com.techchallenge.domain.entity.Order;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

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
        kafkaTemplate.send(topic, order);
    }
}
