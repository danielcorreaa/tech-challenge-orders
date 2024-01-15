package com.techchallenge.infrastructure.persistence.document;

import lombok.*;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class CustomerDocument {
    private String cpf;
    private String name;
    private String email;

}
