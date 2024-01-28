package com.techchallenge.infrastructure.message.producer;

import com.techchallenge.application.gateways.CustomerGateway;
import com.techchallenge.application.gateways.OrderGateway;
import com.techchallenge.application.gateways.ProductGateway;
import com.techchallenge.application.usecases.MessageUseCase;
import com.techchallenge.application.usecases.OrderUseCase;
import com.techchallenge.application.usecases.interactor.MessageUseCaseInteractor;
import com.techchallenge.application.usecases.interactor.OrderUseCaseInteractor;
import com.techchallenge.core.kafka.produce.TopicProducer;
import com.techchallenge.domain.entity.Order;
import com.techchallenge.infrastructure.external.mapper.CustomerDtoMapper;
import com.techchallenge.infrastructure.external.mapper.ProductsDtoMapper;
import com.techchallenge.infrastructure.gateways.OrderRepositoryGateway;
import com.techchallenge.infrastructure.persistence.document.OrderDocument;
import com.techchallenge.infrastructure.persistence.mapper.CustomerEntityMapper;
import com.techchallenge.infrastructure.persistence.mapper.OrderEntityMapper;
import com.techchallenge.infrastructure.persistence.mapper.ProductEntityMapper;
import com.techchallenge.infrastructure.persistence.repository.OrderRepository;
import com.techchallenge.utils.OrderHelper;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class OrderProduceTest {

    OrderProduce orderProduce;
    MessageUseCase messageUseCase;
    OrderUseCase orderUseCase;

    @Mock
    TopicProducer<Order> topicProducer;

    OrderGateway orderGateway;

    @Mock
    CustomerGateway customerGateway;

    @Mock
    ProductGateway productGateway;

    @Mock
    OrderRepository repository;
    OrderEntityMapper orderEntityMapper;
    CustomerDtoMapper customerDtoMapper;
    ProductsDtoMapper productsDtoMapper;

    CustomerEntityMapper custumerEntityMapper;

    ProductEntityMapper productEntityMapper;
    OrderHelper mock;

    @BeforeEach
    void init(){
        productEntityMapper = new ProductEntityMapper();
        custumerEntityMapper = new CustomerEntityMapper();
        productsDtoMapper = new ProductsDtoMapper();
        customerDtoMapper = new CustomerDtoMapper();
        orderEntityMapper = new OrderEntityMapper(custumerEntityMapper, productEntityMapper);
        orderGateway = new OrderRepositoryGateway(repository, orderEntityMapper);
        orderUseCase = new OrderUseCaseInteractor(orderGateway, customerGateway, productGateway);
        messageUseCase = new MessageUseCaseInteractor(topicProducer);
        orderProduce = new OrderProduce(messageUseCase, orderUseCase);
        mock = new OrderHelper(orderEntityMapper, customerDtoMapper, productsDtoMapper);
    }

    @Test
    void testProcess_whenExistsOrderToProcess() {
        List<OrderDocument> documents = mock.getOrderDocuments();
        when(repository.findOrdersNotSent()).thenReturn(mock.getOrderDocuments());
        List<Order> orderList = orderEntityMapper.toOrderList(documents);
        for (Order order  :orderList) {
            ProducerRecord<String, Order> producerRecord =
                    new ProducerRecord<>("test", order);
            RecordMetadata recordMetadata = mock(RecordMetadata.class);
            SendResult<String, Order> sendResult = new SendResult<>(producerRecord, recordMetadata);
            when(topicProducer.produce(any(String.class), any(Order.class))).thenReturn(sendResult);
        }
        orderProduce.process();
        verify(repository, times(1)).findOrdersNotSent();
        verify(topicProducer, times(5)).produce(anyString(), any(Order.class));
        verify(repository, times(5)).save(any(OrderDocument.class));
    }

    @Test
    void testProcess_whenNotExistsOrderToProcess() {
        List<OrderDocument> documents = mock.getOrderDocuments();
        when(repository.findOrdersNotSent()).thenReturn(List.of());
        orderProduce.process();
        verify(repository, times(1)).findOrdersNotSent();
        verify(topicProducer, never()).produce(anyString(), any(Order.class));
        verify(repository, never()).save(any(OrderDocument.class));
    }
}