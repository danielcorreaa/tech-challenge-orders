package com.techchallenge.config;

import com.techchallenge.application.gateways.*;
import com.techchallenge.application.usecases.OrderUseCase;
import com.techchallenge.application.usecases.interactor.OrderUseCaseInteractor;
import com.techchallenge.infrastructure.message.consumer.PaymentConsumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

	@Bean
	public OrderUseCase orderUseCase(OrderGateway orderGateway, CustomerGateway customerGateway,
			ProductGateway productGateway) {
		return new OrderUseCaseInteractor(orderGateway, customerGateway, productGateway);
	}


	
}
