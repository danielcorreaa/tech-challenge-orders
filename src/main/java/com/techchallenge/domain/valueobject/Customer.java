package com.techchallenge.domain.valueobject;


public class Customer {

	private String cpf;
	private String name;	
	private String email;

	public Customer(String cpf, String name, String email) {
		super();
		this.cpf = cpf;
		this.name = name;	
		this.email = email;
	}

	public String getCpf() {
		return cpf;
	}
	public String getName() {
		return name;
	}
	public String getEmail() {
		return email;
	}

}
