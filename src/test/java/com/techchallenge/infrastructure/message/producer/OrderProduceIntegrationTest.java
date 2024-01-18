package com.techchallenge.infrastructure.message.producer;

import com.techchallenge.KafkaTestConfig;
import com.techchallenge.application.usecases.OrderUseCase;
import com.techchallenge.domain.entity.Order;
import com.techchallenge.infrastructure.external.mapper.CustomerDtoMapper;
import com.techchallenge.infrastructure.external.mapper.ProductsDtoMapper;
import com.techchallenge.infrastructure.persistence.document.OrderDocument;
import com.techchallenge.infrastructure.persistence.mapper.CustomerEntityMapper;
import com.techchallenge.infrastructure.persistence.mapper.OrderEntityMapper;
import com.techchallenge.infrastructure.persistence.mapper.ProductEntityMapper;
import com.techchallenge.infrastructure.persistence.repository.OrderRepository;
import com.techchallenge.utils.ObjectMock;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration( classes = {KafkaTestConfig.class})
@TestPropertySource(locations = {"classpath:application-test.properties"})
@Testcontainers
class OrderProduceIntegrationTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:6.0.2"))
            .waitingFor(Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(20)));
    @Container
    static KafkaContainer kafkaContainer =
            new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.4.0"));
    @DynamicPropertySource
    static void overrrideMongoDBContainerProperties(DynamicPropertyRegistry registry){
        registry.add("spring.data.mongodb.host", mongoDBContainer::getHost);
        registry.add("spring.data.mongodb.port", mongoDBContainer::getFirstMappedPort);

        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);

    }
    @BeforeAll
    static void setUp(){
        mongoDBContainer.withReuse(true);
        mongoDBContainer.start();
        kafkaContainer.withReuse(true);
        kafkaContainer.start();
    }
    @AfterAll
    static void setDown(){
        mongoDBContainer.stop();
        kafkaContainer.stop();
    }

    ObjectMock mock;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderProduce orderProduce;
    @Autowired
    ProductsDtoMapper productsDtoMapper;
    @Autowired
    CustomerDtoMapper customerDtoMapper;
    @Autowired
    OrderEntityMapper orderEntityMapper;
    @Autowired
    OrderUseCase orderUseCase;


    @BeforeEach
    void init(){
        mock = new ObjectMock(orderEntityMapper, customerDtoMapper, productsDtoMapper);
        clear();
    }

    @Test
    void testProcess_whenExistsOrderToProcess() {
       List<OrderDocument> documents = loadOrder();
       orderProduce.process();
       List<Order> maybeOrdersWasProcess = orderUseCase.findOrdersNotSent();
       List<Order> allOrderAfterProcess = orderUseCase.findAllByStatusAndDate();
       assertEquals(0,maybeOrdersWasProcess.size());
       List<Boolean> list = allOrderAfterProcess.stream().map(orde -> orde.getSent()).toList();
       list.forEach( x  -> {
           assertTrue(x);
       });
    }

    @Test
    void testProcess_whenNotExistsOrderToProcess() {
        orderProduce.process();
        List<Order> orders = orderUseCase.findOrdersNotSent();
        assertEquals(0,orders.size());

    }

    void clear(){
        orderRepository.deleteAll();
    }

    private List<OrderDocument> loadOrder() {
        List<OrderDocument> response = new ArrayList<>();
        int cont = 0;
        for (OrderDocument document :mock.getOrderDocuments()) {
            document.setId("00989"+cont);
            OrderDocument save = orderRepository.save(document);
            response.add(save);
        }
        return response;
    }
}