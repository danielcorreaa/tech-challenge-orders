package com.techchallenge.application.usecases.interactor;

import com.techchallenge.application.gateways.CustomerGateway;
import com.techchallenge.application.gateways.OrderGateway;
import com.techchallenge.application.gateways.ProductGateway;
import com.techchallenge.application.usecases.OrderUseCase;
import com.techchallenge.core.exceptions.NotFoundException;
import com.techchallenge.core.response.Result;
import com.techchallenge.domain.entity.Order;
import com.techchallenge.domain.valueobject.Customer;
import com.techchallenge.domain.valueobject.Product;

import java.util.List;
import java.util.Optional;


public class OrderUseCaseInteractor implements OrderUseCase {

	private OrderGateway orderGateway;
	private CustomerGateway customerGateway;
	private ProductGateway productGateway;

	public OrderUseCaseInteractor(OrderGateway orderGateway, CustomerGateway customerGateway,
                                  ProductGateway productGateway) {
		super();
		this.orderGateway = orderGateway;
		this.customerGateway = customerGateway;
		this.productGateway = productGateway;
	}

	public Order insert(String cpf, List<String> productsId) {
		Customer customer = null;
		if(Boolean.TRUE.equals(iaNotNullOrEmpty(cpf))){
			 customer = customerGateway.findByCpf(cpf)
					 .orElseThrow(() -> new NotFoundException("Customer not found for cpf: "+cpf));
		}
		List<Product> products = productGateway.findByIds(productsId)
				.orElseThrow(() -> new NotFoundException("Any product found!"));

		Order order = new Order().startOrder(customer, products);
		order = orderGateway.insert(order);
		return order;
	}

	private Boolean iaNotNullOrEmpty(String cpf) {
		return !Optional.ofNullable(cpf).orElse("").isEmpty();
	}

	public Result<List<Order>> findAll(int page, int size) {
		return orderGateway.findAll(page, size);
	}


	@Override
	public List<Order> findOrdersNotSent() {
		return orderGateway.findOrdersNotSent();
	}

	@Override
	public void update(Order order) {
		orderGateway.update(order);
	}

	public Order findById(String id) {
		return orderGateway.findbyId(id).orElseThrow(() -> new NotFoundException("Order not found!"));
	}

	@Override
	public List<Order> findAllByStatusAndDate() {
		return orderGateway.findByStatusAndDate();
	}

}
