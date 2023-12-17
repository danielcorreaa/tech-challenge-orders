package com.techchallenge.infrastructure.persistence.document;

import com.techchallenge.domain.valueobject.Customer;
import com.techchallenge.domain.valueobject.Product;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Document(collection = "orderdb" )
public class OrderDocument {
    @Id
    private String id;
    private Customer customer;
    private List<Product> products;
    private LocalDateTime dateOrderInit;
    private LocalDateTime dateOrdernFinish;
    private String statusOrder;

}
