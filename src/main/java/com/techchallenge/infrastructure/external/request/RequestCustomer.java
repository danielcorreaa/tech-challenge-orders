package com.techchallenge.infrastructure.external.request;

import com.techchallenge.core.response.Result;
import com.techchallenge.infrastructure.external.dtos.CustomerDto;
import com.techchallenge.infrastructure.external.dtos.ProductDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(value = "customers", url = "${api.customers.url}", path = "${api.customers.path}" )
public interface RequestCustomer {

    @GetMapping(path = "/{cpf}")
    Result<CustomerDto> findByCpf(@PathVariable String cpf);
}
