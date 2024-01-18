package com.techchallenge.application.gateways;

import com.techchallenge.core.response.Result;
import com.techchallenge.domain.entity.Order;

import java.util.List;
import java.util.Optional;

public interface OrderGateway {
	
	Order insert(Order order);

	Result<List<Order>> findAll(int page, int size);

	List<Order> findByStatusOrder(String recebido);

	Optional<Order> findbyId(String id);

	List<Order> findByStatusAndDate();


	List<Order> findOrdersNotSent();

	void update(Order order);
}
