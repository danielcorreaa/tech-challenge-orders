package com.techchallenge.infrastructure.message.consumer;

import com.techchallenge.KafkaTestConfig;
import com.techchallenge.application.usecases.OrderUseCase;
import com.techchallenge.core.kafka.KafkaProducerConfig;
import com.techchallenge.core.response.JsonUtils;
import com.techchallenge.core.response.ObjectMapperConfig;
import com.techchallenge.core.utils.FileUtils;
import com.techchallenge.domain.entity.Order;
import com.techchallenge.infrastructure.external.mapper.CustomerDtoMapper;
import com.techchallenge.infrastructure.external.mapper.ProductsDtoMapper;
import com.techchallenge.infrastructure.message.consumer.dto.StatusDto;
import com.techchallenge.infrastructure.persistence.document.OrderDocument;
import com.techchallenge.infrastructure.persistence.mapper.OrderEntityMapper;
import com.techchallenge.infrastructure.persistence.repository.OrderRepository;
import com.techchallenge.utils.OrderHelper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.ProducerFactory;
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
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
@ContextConfiguration( classes = {KafkaTestConfig.class})
@TestPropertySource(locations = {"classpath:application-test.properties"})
@Testcontainers
class ProductionConsumerIT {

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

    OrderHelper mock;

    JsonUtils jsonUtils;

    @Autowired
    KafkaProducerConfig produce;

    @Value(value = "${kafka.topic.consumer.production.status}")
    String topic;

    @Autowired
    OrderEntityMapper orderEntityMapper;
    @Autowired
    CustomerDtoMapper customerDtoMapper;
    @Autowired
    ProductsDtoMapper productsDtoMapper;

    @Autowired
    ProductionConsumer productionConsumer;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderUseCase orderUseCase;


    @BeforeEach
    void init(){
        jsonUtils = new JsonUtils(new ObjectMapperConfig().objectMapper());
        mock = new OrderHelper(orderEntityMapper, customerDtoMapper, productsDtoMapper);
        clear();

    }

    @Test
    void testListenProductionAndUpdateStatusOrder() throws InterruptedException {
        List<OrderDocument> documents =  loadOrder();
        StatusDto status = jsonUtils.parse(new FileUtils().getFile("/data/status.json"),
                StatusDto.class).orElse(null);
        kafkaProducer().kafkaTemplate().send(topic, status);

        boolean messageConsumed = productionConsumer.getLatch().await(10, TimeUnit.SECONDS);

        Order byId = orderUseCase.findById(status.orderId());
        assertEquals("EM_PREPARACAO",byId.getStatusOrderString());

        assertTrue(messageConsumed);
    }



    @Test
    void testListenProductionAndUpdateStatusOrder_idNotFound() throws InterruptedException {
        StatusDto status = jsonUtils.parse(new FileUtils().getFile("/data/status.json"),
                StatusDto.class).orElse(null);
        kafkaProducer().kafkaTemplate().send(topic, status);

        boolean messageConsumed = productionConsumer.getLatch().await(10, TimeUnit.SECONDS);

        assertFalse(messageConsumed);
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

    private void clear() {
        orderRepository.deleteAll();
    }

    public KafkaProducerConfig kafkaProducer(){
        return new KafkaProducerConfig(kafkaContainer.getBootstrapServers());
    }


    public ProducerFactory<String, StatusDto> producerFactory(){
        return kafkaProducer().producerFactory();
    }



}