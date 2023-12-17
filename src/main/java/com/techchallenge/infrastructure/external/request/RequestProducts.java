package com.techchallenge.infrastructure.external.request;

import com.techchallenge.core.response.Result;
import com.techchallenge.infrastructure.external.dtos.ProductDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "products", url = "${api.products.url}", path = "${api.products.path}" )
public interface RequestProducts {

    @GetMapping
    Result<List<ProductDto>> findBySkus(@RequestParam List<String> skus);
}
