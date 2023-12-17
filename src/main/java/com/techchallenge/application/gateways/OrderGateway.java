package com.techchallenge.application.gateways;

import com.techchallenge.domain.entity.Order;

import java.util.List;
import java.util.Optional;

public interface OrderGateway {
	
	Order insert(Order order);

	Optional<List<Order>> findAll(int page, int size, List<String> sort);

	Optional<List<Order>> findByStatusOrder(String recebido);

	Optional<Order> findbyId(String id);

	List<Order> findByStatusAndDate();


}
