package com.techchallenge.infrastructure.gateways;

import com.techchallenge.application.gateways.CustomerGateway;
import com.techchallenge.core.exceptions.BusinessException;
import com.techchallenge.core.response.Result;
import com.techchallenge.domain.valueobject.Customer;
import com.techchallenge.infrastructure.external.dtos.CustomerDto;
import com.techchallenge.infrastructure.external.mapper.CustomerDtoMapper;
import com.techchallenge.infrastructure.external.request.RequestCustomer;
import feign.FeignException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CustomerGatewayInteractor implements CustomerGateway {

    private RequestCustomer request;
    private CustomerDtoMapper mapper;
    public CustomerGatewayInteractor(RequestCustomer request, CustomerDtoMapper mapper) {
        this.request = request;
        this.mapper = mapper;
    }
    @Override
    public Optional<Customer> findByCpf(String cpf) {
        try {
            Result<CustomerDto> response = request.findByCpf(cpf);
            if (200 == response.getCode()) {
                Customer customer = mapper.toCustomer(response.getBody());
                return Optional.ofNullable(customer);
            }
            return Optional.empty();
        }catch (FeignException fe){
            throw new BusinessException("Fail request to customer api");
        }
    }
}
