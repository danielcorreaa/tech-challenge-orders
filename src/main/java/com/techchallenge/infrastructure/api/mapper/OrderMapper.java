package com.techchallenge.infrastructure.api.mapper;

import com.techchallenge.domain.entity.Order;
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
			// customer = customerMapper.toCustomerResponse(order.getCustomer());
		}
		//List<ProductResponse> products = productMapper.toProductResponseList(order.getProcuts());
		return new OrderResponse(order.getId(), customer, null, order.getInitOrder(), order.getFinishOrder(), order.getMinutesDurationOrder(),
				order.getStatusOrderString());
	
	}

	public List<OrderResponse> toOrderListResponse(List<Order> orders) {
		return orders.stream().map(order -> toOrderResponse(order)).collect(Collectors.toList());
	}

}
