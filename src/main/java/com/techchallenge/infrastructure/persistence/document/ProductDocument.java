package com.techchallenge.infrastructure.persistence.document;

import lombok.*;

import java.math.BigDecimal;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class ProductDocument {

    private String sku;
    private String title;
    private String category;
    private String description;
    private BigDecimal price;
    private String image;
}
