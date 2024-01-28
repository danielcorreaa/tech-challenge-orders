package com.techchallenge.infrastructure.persistence.mapper;

import com.techchallenge.domain.valueobject.Customer;
import com.techchallenge.infrastructure.persistence.document.CustomerDocument;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CustomerEntityMapper {


    public Customer toCustomer(CustomerDocument customerDocument){
        if(Optional.ofNullable(customerDocument).isPresent()) {
            return new Customer(customerDocument.getCpf(), customerDocument.getName(), customerDocument.getEmail());
        }
        return null;
    }

    public CustomerDocument toCustomerDocument(Customer customer){
        if(Optional.ofNullable(customer).isPresent()) {
            return CustomerDocument.builder().cpf(customer.getCpf()).email(customer.getEmail()).name(customer.getName()).build();
        }
        return  null;
    }


}
