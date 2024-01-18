package com.techchallenge.infrastructure.api.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.List;

public record OrderRequest(
		@Getter		
		String cpfCustumer, 
		
		@Getter	
		@NotNull(message = "Products is required!")		
		List<String> products
		) {

}
