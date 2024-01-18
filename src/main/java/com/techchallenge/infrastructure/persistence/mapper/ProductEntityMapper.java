package com.techchallenge.infrastructure.persistence.mapper;

import com.techchallenge.domain.valueobject.Product;
import com.techchallenge.infrastructure.persistence.document.ProductDocument;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductEntityMapper {

    public ProductDocument toProductDocument(Product product){
        return ProductDocument.builder()
                .title(product.getTitle())
                .sku(product.getSku())
                .image(product.getImage())
                .price(product.getPrice())
                .category(product.getCategory())
                .description(product.getDescription()).build();
    }

    public List<ProductDocument> toProductDocuments(List<Product> products){
        return products.stream().map(this::toProductDocument).toList();
    }


    public List<Product> toProducts(List<ProductDocument> productDocuments){
        return productDocuments.stream().map(this::toProduct).toList();
    }

    public Product toProduct(ProductDocument productDocument){
        return new Product(productDocument.getSku(), productDocument.getTitle(), productDocument.getCategory(),
                productDocument.getDescription(), productDocument.getPrice(), productDocument.getImage());
    }

}
