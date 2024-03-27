package com.techchallenge.infrastructure.message.consumer.dto;

public record MessagePaymentDto(String externalReference, String orderStatus, String cpfCustomer ) {

    @Override
    public String toString() {
        return externalReference +" - "+ orderStatus +" - "+cpfCustomer;
    }
}
