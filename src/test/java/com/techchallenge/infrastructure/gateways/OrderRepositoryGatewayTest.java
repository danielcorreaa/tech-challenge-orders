package com.techchallenge.infrastructure.gateways;

import com.techchallenge.MongoTestConfig;
import com.techchallenge.application.gateways.OrderGateway;
import com.techchallenge.core.response.Result;
import com.techchallenge.domain.entity.Order;
import com.techchallenge.infrastructure.external.mapper.CustomerDtoMapper;
import com.techchallenge.infrastructure.external.mapper.ProductsDtoMapper;
import com.techchallenge.infrastructure.persistence.mapper.OrderEntityMapper;
import com.techchallenge.infrastructure.persistence.repository.OrderRepository;
import com.techchallenge.utils.ObjectMock;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration( classes = {MongoTestConfig.class})
@TestPropertySource(locations = "classpath:/application-test.properties")
@Testcontainers
class OrderRepositoryGatewayTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:6.0.2"))
            .waitingFor(Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(20)));

    @DynamicPropertySource
    static void overrrideMongoDBContainerProperties(DynamicPropertyRegistry registry){
        registry.add("spring.data.mongodb.host", mongoDBContainer::getHost);
        registry.add("spring.data.mongodb.port", mongoDBContainer::getFirstMappedPort);
    }


    @BeforeAll
    static void setUp(){
        mongoDBContainer.withReuse(true);
        mongoDBContainer.start();
    }

    @AfterAll
    static void setDown(){
        mongoDBContainer.stop();
    }

    OrderGateway orderGateway;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderEntityMapper orderEntityMapper;

    ObjectMock mock;

    @Autowired
    CustomerDtoMapper customerDtoMapper;

    @Autowired
    ProductsDtoMapper productsDtoMapper;

    @BeforeEach
    void init(){
       orderGateway = new OrderRepositoryGateway(orderRepository, orderEntityMapper);
       mock = new ObjectMock(orderEntityMapper, customerDtoMapper, productsDtoMapper);
       clear();

    }

    @Test
    void testInsertOrder_withCustomer_withProducts() {
        List<String> skus = List.of("5260001", "526002");
        String cpf = "74294115416";
        Order order = mock.getOrder(cpf, skus);
        Order insert = orderGateway.insert(order);
        assertFalse(insert.getSent());
        assertEquals(2, insert.getProducts().size());
        assertNotNull(insert.getCustomer());
    }

    @Test
    void testInsertOrder_withoutCustomer_withProducts() {
        List<String> skus = List.of("5260001", "526002");
        Order order = mock.getOrder(null, skus);
        Order insert = orderGateway.insert(order);
        assertFalse(insert.getSent());
        assertEquals(2, insert.getProducts().size());
        assertNull(insert.getCustomer());
    }

    @Test
    void testFind_withpagination() {
        loadOrder();
        Result<List<Order>> all = orderGateway.findAll(0, 2);
        assertEquals(2, all.getBody().size());
        assertTrue(all.getHasNext());
        assertEquals(5, all.getTotal());
    }

    @Test
    void testGindByStatusOrder() {
        loadOrder();
        List<Order> all = orderGateway.findByStatusOrder("RECEBIDO");
        assertFalse(all.isEmpty());
        assertEquals(5, all.size());
    }

    @Test
    void testGindByStatusOrder_notFound() {
        loadOrder();
        List<Order> all = orderGateway.findByStatusOrder("PRONTO");
        assertTrue(all.isEmpty());
        assertEquals(0, all.size());
    }

    @Test
    void testFindbyId_withSuccess() {
        List<String> skus = List.of("5260001", "526002");
        Order order = mock.getOrder(null, skus);
        Order insert = orderGateway.insert(order);
        String id =  insert.getId();
        Optional<Order> findbyId = orderGateway.findbyId(id);

        assertTrue(findbyId.isPresent());
        assertEquals(2, findbyId.get().getProducts().size());
        assertNull(findbyId.get().getCustomer());
    }

    @Test
    void testFindbyId_noffound() {
        Optional<Order> findbyId = orderGateway.findbyId("123");
        assertFalse(findbyId.isPresent());
    }

    @Test
    void findByStatusAndDate() {
        loadOrderVariationStatus();
        List<Order> byStatusAndDate = orderGateway.findByStatusAndDate();
        assertEquals(4, byStatusAndDate.size());
    }

    @Test
    void findOrdersNotSent() {
        loadOrder();
        List<Order> ordersNotSent = orderGateway.findOrdersNotSent();
        assertFalse(ordersNotSent.isEmpty());
        assertEquals(5, ordersNotSent.size());
    }

    @Test
    void update() {
        List<String> skus = List.of("5260001", "526002");
        Order order = mock.getOrder(null, skus);
        Order insert = orderGateway.insert(order);
        orderGateway.update(insert.changeStatus("EM_PREPARACAO"));
        String id =  insert.getId();
        Optional<Order> findbyId = orderGateway.findbyId(id);
        assertTrue(findbyId.isPresent());
        assertEquals("EM_PREPARACAO", findbyId.get().getStatusOrderString());
    }


    private void loadOrder() {
        mock.getOrderDocuments().forEach( order -> {
            orderRepository.save(order);
        });
    }

    private void loadOrderVariationStatus() {
        mock.getOrderDocumentsVariationStatus().forEach( order -> {
            orderRepository.save(order);
        });
    }

    private void clear() {
        orderRepository.deleteAll();
    }
}