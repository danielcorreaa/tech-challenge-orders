package com.techchallenge.infrastructure.api.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.List;

public record OrderRequest(

		String orderId,
		@Getter		
		String cpfCustumer, 
		
		@Getter	
		@NotNull(message = "Products is required!")		
		List<String> products
		) {

	public OrderRequest(String cpfCustumer, List<String> products) {
		this(null, cpfCustumer, products);
	}
}
