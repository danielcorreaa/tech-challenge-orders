package com.techchallenge.infrastructure.external.mapper;

import com.techchallenge.domain.valueobject.Product;
import com.techchallenge.infrastructure.external.dtos.ProductDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductsDtoMapper {

    public Product toProduct(ProductDto productDto){
        return new Product(productDto.sku(), productDto.title(), productDto.category(), productDto.description(), productDto.price(), productDto.image());
    }

    public List<Product> toProductlist(List<ProductDto> dtos){
       return dtos.stream().map(this::toProduct).toList();
    }
}
