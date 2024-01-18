package com.techchallenge.infrastructure.api.request;

import java.math.BigDecimal;

public record ProductResponse(String id, String title, String category, String description, BigDecimal price, String image) {

}
