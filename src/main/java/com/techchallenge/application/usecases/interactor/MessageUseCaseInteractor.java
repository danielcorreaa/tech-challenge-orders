package com.techchallenge.application.usecases.interactor;

import com.techchallenge.application.usecases.MessageUseCase;
import com.techchallenge.core.kafka.produce.TopicProducer;
import com.techchallenge.domain.entity.Order;
import org.springframework.kafka.support.SendResult;

public class MessageUseCaseInteractor implements MessageUseCase {

    private TopicProducer<Order> topicProducer;

    public MessageUseCaseInteractor(TopicProducer<Order> topicProducer) {
        this.topicProducer = topicProducer;
    }

    @Override
    public SendResult<String, Order> send(Order message) {
        return topicProducer.produce(message.getId(),message);
    }
}
