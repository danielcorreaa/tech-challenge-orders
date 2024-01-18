package com.techchallenge.application.usecases;

import com.techchallenge.core.response.Result;
import com.techchallenge.domain.entity.Order;

import java.util.List;

public interface OrderUseCase {

	Order insert(String cpf, List<String> productsId);	
	Order findById(String id);
	Result<List<Order>> findAll(int page, int size);
	List<Order> findAllByStatusAndDate();

	List<Order> findOrdersNotSent();
	void update(Order order);
}
