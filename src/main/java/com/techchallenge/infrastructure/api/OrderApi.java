package com.techchallenge.infrastructure.api;

import com.techchallenge.application.usecases.OrderUseCase;
import com.techchallenge.core.response.Result;
import com.techchallenge.domain.entity.Order;
import com.techchallenge.infrastructure.api.mapper.OrderMapper;
import com.techchallenge.infrastructure.api.request.OrderRequest;
import com.techchallenge.infrastructure.api.request.OrderResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("api/v1/orders")
@Tag(name = "Orders API")
public class OrderApi {

	private OrderUseCase orderUseCase;
	private OrderMapper mapper;

	public OrderApi(OrderUseCase orderUseCase, OrderMapper mapper) {
		super();
		this.orderUseCase = orderUseCase;
		this.mapper = mapper;
	}

	@PostMapping( value = "/checkout" )
	public ResponseEntity<Result<OrderResponse>> insert(@RequestBody @Valid OrderRequest request, UriComponentsBuilder uri) {
		Order order = orderUseCase.insert(request);
		UriComponents uriComponents = uri.path("/api/v1/orders/find/{id}").buildAndExpand(order.getId());
		var result = Result.create(mapper.toOrderResponse(order));
		return ResponseEntity.created(uriComponents.toUri()).headers(result.getHeadersNosniff()).body(result);
	}

	
	@GetMapping("/find/{orderId}")
	public ResponseEntity<Result<OrderResponse>> findByid(@PathVariable String orderId) {
		Order order = orderUseCase.findById(orderId);
		return ResponseEntity.ok(Result.ok(mapper.toOrderResponse(order)));
	}
	
	@GetMapping
	public ResponseEntity<Result<List<OrderResponse>>> findAll(
			@RequestParam(value = "page", required = true, defaultValue = "0") int page,
			@RequestParam(value = "size", required = true, defaultValue = "10") int size) {
		Result<List<Order>> all = orderUseCase.findAll(page, size);
		return ResponseEntity.ok(Result.ok(mapper.toOrderListResponse(all.getBody()), all.getHasNext(), all.getTotal()));
	}

	
	@GetMapping("/sorted")
	public ResponseEntity<Result<List<OrderResponse>>> findByOrderAndDate() {
		List<Order> findAll = orderUseCase.findAllByStatusAndDate();
		return ResponseEntity.ok(Result.ok(mapper.toOrderListResponse(findAll)));
	}
}
