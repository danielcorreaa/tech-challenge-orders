package com.techchallenge.infrastructure.api;

import com.techchallenge.application.usecases.OrderUseCase;
import com.techchallenge.core.response.Result;
import com.techchallenge.domain.entity.Order;
import com.techchallenge.infrastructure.api.mapper.OrderMapper;
import com.techchallenge.infrastructure.api.request.OrderRequest;
import com.techchallenge.infrastructure.api.request.OrderResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("api/v1/orders")
public class OrderApi {

	private OrderUseCase orderUseCase;
	private OrderMapper mapper;

	public OrderApi(OrderUseCase orderUseCase, OrderMapper mapper) {
		super();
		this.orderUseCase = orderUseCase;
		this.mapper = mapper;
	}

	@PostMapping("/checkout")
	public ResponseEntity<Result<OrderResponse>> insert(@RequestBody @Valid OrderRequest request, UriComponentsBuilder uri) {
		Order order = orderUseCase.insert(request.getCpfCustumer(), request.getProducts());
		UriComponents uriComponents = uri.path("/api/v1/orders/find/{id}").buildAndExpand(order.getId());
		return ResponseEntity.created(uriComponents.toUri()).body(Result.create(mapper.toOrderResponse(order)));
	}

	@PutMapping("/ready/{id}")
	public ResponseEntity<Result<OrderResponse>> ready(@PathVariable String id) {		
		Order order = orderUseCase.ready(id);
		return ResponseEntity.ok(Result.ok(mapper.toOrderResponse(order)));
	}

	@PutMapping("/finish/{id}")
	public ResponseEntity<Result<OrderResponse>> finish(@PathVariable String id) {		
		Order order = orderUseCase.finish(id);
		return ResponseEntity.ok(Result.ok(mapper.toOrderResponse(order)));
	}
	
	@GetMapping("/find/{id}")
	public ResponseEntity<Result<OrderResponse>> findByid(@PathVariable String id) {		
		Order order = orderUseCase.findById(id);
		return ResponseEntity.ok(Result.ok(mapper.toOrderResponse(order)));
	}
	
	@GetMapping
	public ResponseEntity<Result<List<OrderResponse>>> findAll(
			@RequestParam(value = "page", required = false, defaultValue = "0") int page,
			@RequestParam(value = "size", required = false, defaultValue = "10") int size) {
		List<Order> findAll = orderUseCase.findAll(page, size, null);		
		return ResponseEntity.ok(Result.ok(mapper.toOrderListResponse(findAll)));
	}

	
	@GetMapping("/sorted")
	public ResponseEntity<Result<List<OrderResponse>>> findByOrderAndDate() {
		List<Order> findAll = orderUseCase.findAllByStatusAndDate();
		return ResponseEntity.ok(Result.ok(mapper.toOrderListResponse(findAll)));
	}
}
