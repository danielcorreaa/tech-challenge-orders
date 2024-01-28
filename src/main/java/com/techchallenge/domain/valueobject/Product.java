package com.techchallenge.domain.valueobject;


import java.math.BigDecimal;

public class Product {

	private String sku;
	private String title;
	private String category;
	private String description;
	private BigDecimal price;
	private String image;

	public Product(String sku, String title, String category, String description, BigDecimal price, String image) {
		this.category = category;
		this.sku = sku;
		this.title = title;		
		this.description = description;
		this.price = price;
		this.image = image;
	}

	public String getSku() {
		return sku;
	}

	public String getTitle() {
		return title;
	}

	public String getCategory() {
		return category;
	}

	public String getDescription() {
		return description;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public String getImage() {
		return image;
	}
}
