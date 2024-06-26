package com.techchallenge.domain.enums;

import java.util.Arrays;

public enum StatusOrder {
	RECEBIDO, EM_PREPARACAO, PRONTO, FINALIZADO, CANCELADO;

	public static StatusOrder getByName(String name){
		return Arrays.stream(values()).filter(v -> v.name().equals(name)).findFirst()
				.orElseThrow(() -> new IllegalArgumentException("Invalid Status Order!"));
	}


	
}
