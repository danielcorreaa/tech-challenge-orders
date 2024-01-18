package com.techchallenge.application.gateways;

import com.techchallenge.domain.valueobject.Product;
import java.util.List;
import java.util.Optional;


public interface ProductGateway {
	Optional<List<Product>> findByIds(List<String> productsId);

}
