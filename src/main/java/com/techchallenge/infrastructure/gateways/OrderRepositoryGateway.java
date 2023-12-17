package com.techchallenge.infrastructure.gateways;

import com.techchallenge.application.gateways.OrderGateway;
import com.techchallenge.domain.entity.Order;
import com.techchallenge.infrastructure.persistence.document.OrderDocument;
import com.techchallenge.infrastructure.persistence.mapper.OrderEntityMapper;
import com.techchallenge.infrastructure.persistence.repository.OrderRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class OrderRepositoryGateway implements OrderGateway {

	private OrderRepository repository;
	private OrderEntityMapper mapper;

	public OrderRepositoryGateway(OrderRepository repository, OrderEntityMapper mapper) {
		super();
		this.repository = repository;
		this.mapper = mapper;
	}

	@Override
	public Order insert(Order order) {
		OrderDocument orderDocument = mapper.toOrderEntity(order);
		orderDocument = repository.save(orderDocument);
		return mapper.toOrder(orderDocument);
	}

	@Override
	public Optional<List<Order>> findAll(int page, int size, List<String> sort) {		
		Pageable pageable = Pageable.ofSize(size).withPage(page);
		var allOrders = repository.findAllOrderByDateOrderInit(pageable);		
		var allOrderByDate = Optional.ofNullable(allOrders.getContent());
		return allOrderByDate.map(all -> mapper.toOrderList(all));
	}

	@Override
	public Optional<List<Order>> findByStatusOrder(String recebido) {
		var findByOrderStatus = repository.findByStatusOrder(recebido);
		return findByOrderStatus.map(all -> mapper.toOrderList(all));
	}

	public Optional<Order> findbyId(String id) {
		var findByOrderStatus = repository.findById(id);
		return findByOrderStatus.map(all -> mapper.toOrder(all));
	}

	@Override
	public List<Order> findByStatusAndDate() {
		Optional<List<OrderDocument>> findByStatusAndDate = null;//repository.findByStatusAndDate();
		return mapper.toOrderList(findByStatusAndDate.orElse(List.of()));
	}

}
