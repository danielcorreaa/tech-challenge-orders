package com.techchallenge.infrastructure.message.consumer;

import com.techchallenge.application.gateways.CustomerGateway;
import com.techchallenge.application.gateways.OrderGateway;
import com.techchallenge.application.gateways.ProductGateway;
import com.techchallenge.application.usecases.OrderUseCase;
import com.techchallenge.application.usecases.interactor.OrderUseCaseInteractor;
import com.techchallenge.core.exceptions.BusinessException;
import com.techchallenge.core.response.JsonUtils;
import com.techchallenge.infrastructure.external.mapper.CustomerDtoMapper;
import com.techchallenge.infrastructure.external.mapper.ProductsDtoMapper;
import com.techchallenge.infrastructure.gateways.OrderRepositoryGateway;
import com.techchallenge.infrastructure.message.consumer.dto.StatusDto;
import com.techchallenge.infrastructure.persistence.document.OrderDocument;
import com.techchallenge.infrastructure.persistence.mapper.CustomerEntityMapper;
import com.techchallenge.infrastructure.persistence.mapper.OrderEntityMapper;
import com.techchallenge.infrastructure.persistence.mapper.ProductEntityMapper;
import com.techchallenge.infrastructure.persistence.repository.OrderRepository;
import com.techchallenge.utils.OrderHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(SpringExtension.class)
class ProductionConsumerTest {

    ProductionConsumer productionConsumer;
    OrderUseCase orderUseCase;

    OrderGateway orderGateway;

    @Mock
    CustomerGateway customerGateway;

    @Mock
    ProductGateway productGateway;

    @Mock
    OrderRepository repository;
    OrderEntityMapper mapper;

    @Mock
    CustomerEntityMapper custumerEntityMapper;

    @Mock
    ProductEntityMapper productEntityMapper;

    JsonUtils jsonUtils;

    OrderHelper mock;

    OrderEntityMapper orderEntityMapper;
    CustomerDtoMapper customerDtoMapper;
    ProductsDtoMapper productsDtoMapper;

    @BeforeEach
    void init(){
        productsDtoMapper = new ProductsDtoMapper();
        customerDtoMapper = new CustomerDtoMapper();
        orderEntityMapper = new OrderEntityMapper(custumerEntityMapper, productEntityMapper);
        mock = new OrderHelper(orderEntityMapper, customerDtoMapper, productsDtoMapper);
        mapper = new OrderEntityMapper(custumerEntityMapper, productEntityMapper);
        orderGateway = new OrderRepositoryGateway(repository, mapper);
        orderUseCase = new OrderUseCaseInteractor(orderGateway, customerGateway, productGateway);
        productionConsumer = new ProductionConsumer(orderUseCase);
    }

    @Test
    void testListenProductionAndUpdateStatusOrder(){
        List<String> skus = List.of("2253001", "2253002");
        String cpf = "37465505569";
        OrderDocument orderDocument = mock.getOrderDocument(cpf, skus);
        String id = orderDocument.getId();
        when(repository.findById(id)).thenReturn(Optional.of(orderDocument));
        when(repository.save(any(OrderDocument.class))).thenReturn(orderDocument);
        Acknowledgment ack = spy(Acknowledgment.class);
        StatusDto record = new StatusDto(id, "EM_PREPARACAO");
        productionConsumer.listenStatus(record, ack);

        verify(repository, times(1)).findById(id);
        verify(repository, times(1)).save(any(OrderDocument.class));
        verify(ack, times(1)).acknowledge();
    }

    @Test
    void testListenProductionAndUpdateStatusOrder_idNotFound(){
        List<String> skus = List.of("2253001", "2253002");
        String cpf = "37465505569";
        OrderDocument orderDocument = mock.getOrderDocument(cpf, skus);
        String id = orderDocument.getId();
        when(repository.findById(id)).thenReturn(Optional.empty());

        Acknowledgment ack = spy(Acknowledgment.class);
        StatusDto record = new StatusDto(id, "EM_PREPARACAO");
        var ex = assertThrows(BusinessException.class, () -> productionConsumer.listenStatus(record, ack));

        assertEquals("Order not found!", ex.getMessage());
        verify(repository, times(1)).findById(id);
        verify(repository, never()).save(any(OrderDocument.class));
        verify(ack, never()).acknowledge();
    }




}