package com.techchallenge.infrastructure.persistence.mapper;

import com.techchallenge.domain.entity.Order;
import com.techchallenge.domain.valueobject.Customer;
import com.techchallenge.domain.valueobject.Product;
import com.techchallenge.infrastructure.persistence.document.CustomerDocument;
import com.techchallenge.infrastructure.persistence.document.OrderDocument;
import com.techchallenge.infrastructure.persistence.document.ProductDocument;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class OrderEntityMapper {

	private CustomerEntityMapper custumerEntityMapper;
	private ProductEntityMapper productEntityMapper;

	public OrderEntityMapper(CustomerEntityMapper custumerEntityMapper, ProductEntityMapper productEntityMapper) {
		super();
		this.custumerEntityMapper = custumerEntityMapper;
		this.productEntityMapper = productEntityMapper;
	}

	public OrderDocument toOrderEntity(Order order) {
		return OrderDocument.builder()
				.id(order.getId())
				.dateOrderInit(order.getInitOrder())
				.statusOrder(order.getStatusOrderString())
				.products(toProductDocuments(order.getProducts()))
				.customer(toCustomerDocument(order.getCustomer()))
				.dateOrdernFinish(order.getFinishOrder())
				.sent(order.getSent())
				.build();
	}

	public Order toOrder(OrderDocument document) {
		return Order.convert(document.getId(), toCustomer(document.getCustomer()), toProducts(document.getProducts()),
				document.getDateOrderInit(), document.getDateOrdernFinish(), document.getStatusOrder(), document.getSent() );
	}

	public List<Order> toOrderList(List<OrderDocument> all) {
		return all.stream().map(order -> toOrder(order)).collect(Collectors.toList());
	}

	public CustomerDocument toCustomerDocument(Customer customer){
		if(Optional.ofNullable(customer).isPresent()) {
			return CustomerDocument.builder().cpf(customer.getCpf()).email(customer.getEmail()).name(customer.getName()).build();
		}
		return  null;
	}

	public List<ProductDocument> toProductDocuments(List<Product> products){
		return products.stream().map(this::toProductDocument).toList();
	}

	public ProductDocument toProductDocument(Product product){
		return ProductDocument.builder()
				.title(product.getTitle())
				.sku(product.getSku())
				.image(product.getImage())
				.price(product.getPrice())
				.category(product.getCategory())
				.description(product.getDescription()).build();
	}

	public Customer toCustomer(CustomerDocument customerDocument){
		if(Optional.ofNullable(customerDocument).isPresent()) {
			return new Customer(customerDocument.getCpf(), customerDocument.getName(), customerDocument.getEmail());
		}
		return null;
	}

	public List<Product> toProducts(List<ProductDocument> productDocuments){
		return productDocuments.stream().map(this::toProduct).toList();
	}

	public Product toProduct(ProductDocument productDocument){
		return new Product(productDocument.getSku(), productDocument.getTitle(), productDocument.getCategory(),
				productDocument.getDescription(), productDocument.getPrice(), productDocument.getImage());
	}

}
