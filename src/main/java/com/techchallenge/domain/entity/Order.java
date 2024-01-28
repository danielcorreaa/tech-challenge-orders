package com.techchallenge.domain.entity;

import com.techchallenge.domain.enums.StatusOrder;
import com.techchallenge.domain.valueobject.Customer;
import com.techchallenge.domain.valueobject.Product;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

public class Order {

	private String id;
	private Customer customer;
	private List<Product> products;
	private LocalDateTime dateOrderInit;
	private LocalDateTime dateOrderFinish;
	private Long minutesDurationOrder;
	private StatusOrder statusOrder;
	private Boolean sent;

	public Order() {
		super();
	}
	private Order(String id, Customer customer, List<Product> products, LocalDateTime dateOrderInit,
                  LocalDateTime dateOrdernFinish, String statusOrder, Boolean sent) {
		this.id = id;
		this.customer = customer;
		this.products = products;
		this.dateOrderInit = dateOrderInit;
		this.dateOrderFinish = dateOrdernFinish;
		this.statusOrder = StatusOrder.getByName(statusOrder);
		this.sent = sent;
	}
	
	public Order startOrder(Customer customer, List<Product> procuts, String id) {
		this.id = id;
		this.customer = customer;
		this.products = procuts;
		this.dateOrderInit = LocalDateTime.now();
		this.statusOrder = StatusOrder.RECEBIDO;
		this.sent = Boolean.FALSE;
		return this;
	}

	public Order startOrder(Customer customer, List<Product> procuts) {
		this.id = new ObjectId().toString();
		this.customer = customer;
		this.products = procuts;
		this.dateOrderInit = LocalDateTime.now();
		this.statusOrder = StatusOrder.RECEBIDO;
		this.sent = Boolean.FALSE;
		return this;
	}

	public Order changeStatus(String status) {
		this.statusOrder = StatusOrder.getByName(status);
		if("FINALIZADO".equals(status)){
			this.dateOrderFinish = LocalDateTime.now();
		}
		return this;
	}

	public static final Order convert(String id, Customer customer, List<Product> products, LocalDateTime dateOrderInit,
			LocalDateTime dateOrdernFinish, String statusOrder, Boolean sent) {
		return new Order(id, customer, products, dateOrderInit, dateOrdernFinish, statusOrder, sent);
	}

	public String getId() {
		return id;
	}
	
	public Customer getCustomer() {
		return customer;
	}

	public List<Product> getProducts() {
		return products;
	}

	public LocalDateTime getInitOrder() {
		return dateOrderInit;
	}

	public LocalDateTime getFinishOrder() {
		return dateOrderFinish;
	}

	public Long getMinutesDurationOrder() {
		if(Optional.ofNullable(getFinishOrder()).isPresent()) {
			this.minutesDurationOrder = this.dateOrderInit.until(getFinishOrder(), ChronoUnit.MINUTES);
			return minutesDurationOrder;
		}
		this.minutesDurationOrder = this.dateOrderInit.until(LocalDateTime.now(), ChronoUnit.MINUTES);
		return minutesDurationOrder;
	}

	public LocalDateTime getDateOrderInit() {
		return dateOrderInit;
	}

	public LocalDateTime getDateOrderFinish() {
		return dateOrderFinish;
	}

	public Optional<StatusOrder> getStatusOrder() {
		return Optional.ofNullable(statusOrder);
	}

	public String getStatusOrderString() {
		return getStatusOrder().map(Object::toString).orElse("");
	}

	public Boolean getSent() {
		return sent;
	}

	public Order toSend() {
		this.sent = Boolean.TRUE;
		return this;
	}
}
