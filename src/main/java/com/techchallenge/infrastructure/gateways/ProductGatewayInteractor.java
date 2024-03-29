package com.techchallenge.infrastructure.gateways;

import com.techchallenge.application.gateways.ProductGateway;
import com.techchallenge.core.exceptions.BusinessException;
import com.techchallenge.core.response.Result;
import com.techchallenge.domain.valueobject.Product;
import com.techchallenge.infrastructure.external.dtos.ProductDto;
import com.techchallenge.infrastructure.external.mapper.ProductsDtoMapper;
import com.techchallenge.infrastructure.external.request.RequestProducts;
import feign.FeignException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ProductGatewayInteractor implements ProductGateway {

    private RequestProducts request;
    private ProductsDtoMapper mapper;

    public ProductGatewayInteractor(RequestProducts request, ProductsDtoMapper mapper) {
        this.request = request;
        this.mapper = mapper;
    }


    @Override
    public Optional<List<Product>> findByIds(List<String> skus) {
        try {
            Result<List<ProductDto>> response = request.findBySkus(skus);
            if (200 == response.getCode()) {
                List<Product> products = mapper.toProductlist(response.getBody());
                return Optional.ofNullable(products);
            }
            return Optional.empty();
        }catch (FeignException fx){
            throw new BusinessException("Fail request to products api");
        }
    }
}
