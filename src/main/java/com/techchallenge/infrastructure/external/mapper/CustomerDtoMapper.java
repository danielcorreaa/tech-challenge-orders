package com.techchallenge.infrastructure.external.mapper;

import com.techchallenge.domain.valueobject.Customer;
import com.techchallenge.infrastructure.external.dtos.CustomerDto;
import org.springframework.stereotype.Component;

@Component
public class CustomerDtoMapper {

    public Customer toCustomer(CustomerDto customerDto){
       return new Customer(customerDto.cpf(), customerDto.name(), customerDto.email());
    }
}
