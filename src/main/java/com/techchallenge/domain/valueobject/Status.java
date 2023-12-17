package com.techchallenge.domain.valueobject;


import com.techchallenge.core.exceptions.BusinessException;
import com.techchallenge.domain.entity.Order;
import com.techchallenge.domain.enums.StatusOrder;


public class Status {
	
	private Status() {}

	
	public static final StatusOrder received(Order order) {
		order.getStatusOrder().ifPresent(status -> new BusinessException("Invalid status to order"));
		return StatusOrder.RECEBIDO;
		
	}

	public static StatusOrder preparation(Order order) {		
		return order.getStatusOrder()
				.filter( status -> status.equals(StatusOrder.RECEBIDO))
				.map(status -> StatusOrder.EM_PREPARACAO )
				.orElseThrow(() -> new BusinessException("Invalid status to order"));
	}

	public static StatusOrder ready(Order order) {		
		return order.getStatusOrder()
				.filter( status -> status.equals(StatusOrder.EM_PREPARACAO))
				.map(status -> StatusOrder.PRONTO )
				.orElseThrow(() -> new BusinessException("Invalid status to order"));
	}

	public static StatusOrder finalize(Order order) {		
		return order.getStatusOrder()
				.filter( status -> status.equals(StatusOrder.PRONTO))
				.map(status -> StatusOrder.FINALIZADO )
				.orElseThrow(() -> new BusinessException("Invalid status to order"));
		
	}

}
