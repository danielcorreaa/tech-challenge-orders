package com.techchallenge.infrastructure.persistence.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Document(collection = "orderdb" )
public class OrderDocument {
    @Id
    private String id;
    private CustomerDocument customer;
    private List<ProductDocument> products;
    private LocalDateTime dateOrderInit;
    private LocalDateTime dateOrdernFinish;
    private String statusOrder;
    private Boolean sent;

}
