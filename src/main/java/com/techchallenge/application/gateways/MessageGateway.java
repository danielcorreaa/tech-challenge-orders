package com.techchallenge.application.gateways;

import com.techchallenge.domain.entity.Order;

public interface MessageGateway {
    void send(Order order);
}
