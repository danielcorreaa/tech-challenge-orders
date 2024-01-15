package com.techchallenge.application.usecases;

import com.techchallenge.core.kafka.produce.TopicProducer;
import com.techchallenge.domain.entity.Order;
import org.springframework.kafka.support.SendResult;

public interface MessageUseCase {
    SendResult<String, Order> send(Order message);

}
