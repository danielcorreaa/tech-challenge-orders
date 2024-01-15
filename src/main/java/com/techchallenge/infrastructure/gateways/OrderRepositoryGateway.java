package com.techchallenge.infrastructure.gateways;

import com.techchallenge.application.gateways.OrderGateway;
import com.techchallenge.core.response.Result;
import com.techchallenge.domain.entity.Order;
import com.techchallenge.infrastructure.persistence.document.OrderDocument;
import com.techchallenge.infrastructure.persistence.mapper.OrderEntityMapper;
import com.techchallenge.infrastructure.persistence.repository.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

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
	public Result<List<Order>> findAll(int page, int size) {
		Sort sort = Sort.by(Sort.Direction.ASC, "dateOrderInit");
		Pageable pageable = PageRequest.of(page, size, sort);
		Page<OrderDocument> result = repository.findAll(pageable);
		List<Order> orders = result.getContent().stream().map(orderDoc -> mapper.toOrder(orderDoc)).toList();
		return Result.ok(orders, result.hasNext(), result.getTotalElements());
	}

	@Override
	public List<Order> findByStatusOrder(String recebido) {
		return mapper.toOrderList(repository.findByStatusOrder(recebido));
	}

	public Optional<Order> findbyId(String id) {
		var findByOrderStatus = repository.findById(id);
		return findByOrderStatus.map(all -> mapper.toOrder(all));
	}

	@Override
	public List<Order> findByStatusAndDate() {
		Sort sort = Sort.by(Sort.Direction.ASC, "dateOrderInit");
		List<OrderDocument>findByStatusAndDate = repository.findByStatusOrderAndDateOrderInit(sort);
		return mapper.toOrderList(findByStatusAndDate);
	}

	@Override
	public List<Order> findOrdersNotSent() {
		return mapper.toOrderList(repository.findOrdersNotSent());
	}

	@Override
	public void update(Order order) {
		repository.save(mapper.toOrderEntity(order));
	}

}
