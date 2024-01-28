package com.techchallenge.infrastructure.persistence.mapper;

import com.techchallenge.domain.entity.Order;
import com.techchallenge.infrastructure.persistence.document.OrderDocument;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderEntityMapper {

	private CustomerEntityMapper custumerEntityMapper;
	private ProductEntityMapper productEntityMapper;

	public OrderEntityMapper(CustomerEntityMapper custumerEntityMapper, ProductEntityMapper productEntityMapper) {
		super();
		this.custumerEntityMapper = custumerEntityMapper;
		this.productEntityMapper = productEntityMapper;
	}

	public OrderDocument toOrderEntity(Order order) {
		return OrderDocument.builder()
				.id(order.getId())
				.dateOrderInit(order.getInitOrder())
				.statusOrder(order.getStatusOrderString())
				.products(productEntityMapper.toProductDocuments(order.getProducts()))
				.customer(custumerEntityMapper.toCustomerDocument(order.getCustomer()))
				.dateOrdernFinish(order.getFinishOrder())
				.sent(order.getSent())
				.build();
	}

	public Order toOrder(OrderDocument document) {
		return Order.convert(document.getId(),
				custumerEntityMapper.toCustomer(document.getCustomer()),
				productEntityMapper.toProducts(document.getProducts()),
				document.getDateOrderInit(), document.getDateOrdernFinish(), document.getStatusOrder(), document.getSent() );
	}

	public List<Order> toOrderList(List<OrderDocument> all) {
		return all.stream().map(this::toOrder).toList();
	}

}
