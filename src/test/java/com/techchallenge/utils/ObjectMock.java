package com.techchallenge.utils;

import com.techchallenge.domain.entity.Order;
import com.techchallenge.domain.valueobject.Customer;
import com.techchallenge.domain.valueobject.Product;
import com.techchallenge.infrastructure.external.dtos.CustomerDto;
import com.techchallenge.infrastructure.external.dtos.ProductDto;
import com.techchallenge.infrastructure.external.mapper.CustomerDtoMapper;
import com.techchallenge.infrastructure.external.mapper.ProductsDtoMapper;
import com.techchallenge.infrastructure.persistence.document.OrderDocument;
import com.techchallenge.infrastructure.persistence.mapper.OrderEntityMapper;

import java.math.BigDecimal;
import java.util.List;

public class ObjectMock {

    private OrderEntityMapper orderEntityMapper;
    private CustomerDtoMapper customerDtoMapper;
    private ProductsDtoMapper productsDtoMapper;

    public ObjectMock(OrderEntityMapper orderEntityMapper, CustomerDtoMapper customerDtoMapper, ProductsDtoMapper productsDtoMapper) {
        this.orderEntityMapper = orderEntityMapper;
        this.customerDtoMapper = customerDtoMapper;
        this.productsDtoMapper = productsDtoMapper;
    }

    public CustomerDto getCustomerDto(String cpf){
        return new CustomerDto(cpf, "Doug Funny", "doug@email");
    }

    public ProductDto getProductDto(String sku){
        return new ProductDto(sku, "X Bacon", "LANCHE", "test", new BigDecimal("10.0"), "image");
    }

    public List<ProductDto> getProductDtos(List<String> skus){
        if(null == skus) return null;
        return  skus.stream().map( sku -> getProductDto(sku)).toList();
    }

    public Customer getCustomer(String cpf){
        if(null == cpf) return null;
        return customerDtoMapper.toCustomer(getCustomerDto(cpf));
    }
    public List<Product> getProducts(List<String> skus){
        return productsDtoMapper.toProductlist(getProductDtos(skus));
    }

    public List<OrderDocument> getOrderDocuments(){
        List<String> skus = List.of("2253001", "2253002");
        String cpf = "37465505569";
        Order order1 = getOrder(cpf, skus);

        skus = List.of("2253003", "2253004");
        cpf = "78424674120";
        Order order2 = getOrder(cpf, skus);

        skus = List.of("2253005", "2253006");
        cpf = "22683537964";
        Order order3 = getOrder(cpf, skus);

        skus = List.of("2253005", "2253006");
        cpf = "63258647356";
        Order order4 = getOrder(cpf, skus);


        skus = List.of("2253005", "2253006");
        cpf = "39956866725";
        Order order5 = getOrder(cpf, skus);

        return List.of(
                orderEntityMapper.toOrderEntity(order1),
                orderEntityMapper.toOrderEntity(order2),
                orderEntityMapper.toOrderEntity(order3),
                orderEntityMapper.toOrderEntity(order4),
                orderEntityMapper.toOrderEntity(order5)
        );
    }

    public OrderDocument getOrderDocument(String cpf, List<String> skus){
        return orderEntityMapper.toOrderEntity(getOrder(cpf, skus));
    }
    public Order getOrder(String cpf, List<String> skus) {
        return new Order().startOrder(getCustomer(cpf), getProducts(skus));
    }

    public List<OrderDocument> getOrderDocumentsVariationStatus(){
        List<String> skus = List.of("2253001", "2253002");
        String cpf = "37465505569";
        Order order1 = getOrder(cpf, skus);

        skus = List.of("2253003", "2253004");
        cpf = "78424674120";
        Order order2 = getOrder(cpf, skus);
        order2.changeStatus("PRONTO");

        skus = List.of("2253005", "2253006");
        cpf = "22683537964";
        Order order3 = getOrder(cpf, skus);
        order3.changeStatus("EM_PREPARACAO");

        skus = List.of("2253005", "2253006");
        cpf = "63258647356";
        Order order4 = getOrder(cpf, skus);
        order4.changeStatus("EM_PREPARACAO");

        skus = List.of("2253005", "2253006");
        cpf = "39956866725";
        Order order5 = getOrder(cpf, skus);
        order5.changeStatus("FINALIZADO");

        return List.of(
                orderEntityMapper.toOrderEntity(order1),
                orderEntityMapper.toOrderEntity(order2),
                orderEntityMapper.toOrderEntity(order3),
                orderEntityMapper.toOrderEntity(order4),
                orderEntityMapper.toOrderEntity(order5)
        );
    }
}
