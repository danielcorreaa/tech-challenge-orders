package com.techchallenge.application.gateways;

import com.techchallenge.domain.valueobject.Customer;
import java.util.Optional;

public interface CustomerGateway {

	Optional<Customer> findByCpf(String cpf);


}
