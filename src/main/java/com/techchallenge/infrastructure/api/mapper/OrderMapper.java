package com.techchallenge.infrastructure.api.mapper;

import com.techchallenge.domain.entity.Order;
import com.techchallenge.domain.valueobject.Customer;
import com.techchallenge.domain.valueobject.Product;
import com.techchallenge.infrastructure.api.request.CustomerResponse;
import com.techchallenge.infrastructure.api.request.OrderResponse;
import com.techchallenge.infrastructure.api.request.ProductResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class OrderMapper {


	public OrderResponse toOrderResponse(Order order) {
		CustomerResponse customer = null;
		if(Optional.ofNullable(order.getCustomer()).isPresent()) {
			customer = toCustomerResponse(order.getCustomer());
		}
		List<ProductResponse> products = toProductResponseList(order.getProducts());
		return new OrderResponse(order.getId(), customer, products, order.getInitOrder(), order.getFinishOrder(), order.getMinutesDurationOrder(),
				order.getStatusOrderString());
	
	}

	public List<OrderResponse> toOrderListResponse(List<Order> orders) {
		return orders.stream().map(order -> toOrderResponse(order)).collect(Collectors.toList());
	}

	public ProductResponse toProductResponse(Product product) {
		return new ProductResponse(product.getSku(), product.getTitle(), product.getCategory().toString(),
				product.getDescription(), product.getPrice(), product.getImage());
	}

	public List<ProductResponse> toProductResponseList(List<Product> product) {
		return product.stream().map( p -> toProductResponse(p)).collect(Collectors.toList());
	}

	public CustomerResponse toCustomerResponse(Customer customer) {
		return new CustomerResponse(customer.getCpf(), customer.getName(), customer.getEmail());
	}
}
