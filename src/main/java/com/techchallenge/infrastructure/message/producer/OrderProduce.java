package com.techchallenge.infrastructure.message.producer;

import com.techchallenge.application.usecases.MessageUseCase;
import com.techchallenge.application.usecases.OrderUseCase;
import com.techchallenge.domain.entity.Order;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

@Log4j2
@Configuration
@EnableScheduling
public class OrderProduce {

    private static final long SECOND = 1000;
    private static final long MINUTE = SECOND * 60;

    private MessageUseCase messageUseCase;
    private OrderUseCase orderUseCase;


    public OrderProduce(MessageUseCase messageUseCase, OrderUseCase orderUseCase) {
        this.messageUseCase = messageUseCase;
        this.orderUseCase = orderUseCase;
    }

    @Scheduled(fixedDelay = MINUTE)
    public void process(){
        List<Order> orders = orderUseCase.findOrdersNotSent();
        if(orders.isEmpty()) { log.info("No message to send");}
        orders.forEach( order -> {
            log.info("Message to send: " + order);
            messageUseCase.send(order);
            order.toSend();
            orderUseCase.update(order);
        });
    }
}
