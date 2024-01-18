package com.techchallenge.infrastructure.message.consumer;

import com.techchallenge.application.usecases.OrderUseCase;
import com.techchallenge.core.exceptions.BusinessException;
import com.techchallenge.domain.entity.Order;
import com.techchallenge.domain.enums.StatusOrder;
import com.techchallenge.infrastructure.message.consumer.dto.StatusDto;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;

import java.util.concurrent.CountDownLatch;

@Log4j2
public class ProductionConsumer {

    private OrderUseCase orderUseCase;

    private CountDownLatch latch = new CountDownLatch(1);

    public ProductionConsumer(OrderUseCase orderUseCase) {
        this.orderUseCase = orderUseCase;
    }

    @KafkaListener(topics = "${kafka.topic.consumer.production.status}",
            groupId = "${kafka.topic.consumer.groupId}",
            containerFactory = "kafkaListenerContainerFactoryStatusDto")
    public void
    listenStatus(@Payload StatusDto statusDto, Acknowledgment ack) {
        log.info("Received Message: " + statusDto.toString());
        try {
            Order order = orderUseCase.findById(statusDto.orderId());
            orderUseCase.update(order.changeStatus(statusDto.status()));
            ack.acknowledge();
            latch.countDown();
        } catch (Exception ex){
            log.error("Message not save: "+ ex.getMessage());
            throw new BusinessException(ex.getMessage());
        }
    }

    public void resetLatch() {
        latch = new CountDownLatch(1);
    }

    public CountDownLatch getLatch() {
        return latch;
    }
}
