package com.techchallenge.config;

import com.techchallenge.application.gateways.*;
import com.techchallenge.application.usecases.OrderUseCase;
import com.techchallenge.application.usecases.interactor.OrderUseCaseInteractor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

	@Bean
	public OrderUseCase orderUseCase(OrderGateway orderGateway, CustomerGateway customerGateway,
			ProductGateway productGateway,  MessageGateway messageGateway) {
		return new OrderUseCaseInteractor(orderGateway, customerGateway, productGateway, messageGateway);
	}

	
}
