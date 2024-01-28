package com.techchallenge.infrastructure.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.techchallenge.application.gateways.CustomerGateway;
import com.techchallenge.application.gateways.OrderGateway;
import com.techchallenge.application.gateways.ProductGateway;
import com.techchallenge.application.usecases.OrderUseCase;
import com.techchallenge.application.usecases.interactor.OrderUseCaseInteractor;
import com.techchallenge.core.exceptions.handler.ExceptionHandlerConfig;
import com.techchallenge.core.response.JsonUtils;
import com.techchallenge.core.response.ObjectMapperConfig;
import com.techchallenge.core.response.Result;
import com.techchallenge.domain.entity.Order;
import com.techchallenge.infrastructure.api.mapper.OrderMapper;
import com.techchallenge.infrastructure.api.request.OrderRequest;
import com.techchallenge.infrastructure.api.request.OrderResponse;
import com.techchallenge.infrastructure.external.mapper.CustomerDtoMapper;
import com.techchallenge.infrastructure.external.mapper.ProductsDtoMapper;
import com.techchallenge.infrastructure.external.request.RequestCustomer;
import com.techchallenge.infrastructure.external.request.RequestProducts;
import com.techchallenge.infrastructure.gateways.CustomerGatewayInteractor;
import com.techchallenge.infrastructure.gateways.OrderRepositoryGateway;
import com.techchallenge.infrastructure.gateways.ProductGatewayInteractor;
import com.techchallenge.infrastructure.persistence.document.OrderDocument;
import com.techchallenge.infrastructure.persistence.mapper.CustomerEntityMapper;
import com.techchallenge.infrastructure.persistence.mapper.OrderEntityMapper;
import com.techchallenge.infrastructure.persistence.mapper.ProductEntityMapper;
import com.techchallenge.infrastructure.persistence.repository.OrderRepository;
import com.techchallenge.utils.OrderHelper;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
class OrderApiTest {

    MockMvc mockMvc;

    OrderApi orderApi;

    OrderUseCase orderUseCase;
    OrderMapper orderMapper;

    private JsonUtils jsonUtils;

    OrderGateway orderGateway;
    CustomerGateway customerGateway;
    ProductGateway productGateway;

    @Mock
    private OrderRepository orderRepository;
    private OrderEntityMapper orderEntityMapper;

    CustomerEntityMapper custumerEntityMapper;
    ProductEntityMapper productEntityMapper;

    @Mock
    RequestCustomer requestCustomer;
    CustomerDtoMapper customerDtoMapper;

    @Mock
    RequestProducts requestProducts;
    ProductsDtoMapper productsDtoMapper;

    OrderHelper mock;

    @BeforeEach
    public void init() {
        ObjectMapper objectMapper = new ObjectMapperConfig().objectMapper();
        jsonUtils = new JsonUtils(objectMapper);

        custumerEntityMapper = new CustomerEntityMapper();
        productEntityMapper = new ProductEntityMapper();
        orderEntityMapper = new OrderEntityMapper(custumerEntityMapper, productEntityMapper);

        customerDtoMapper = new CustomerDtoMapper();
        customerGateway = new CustomerGatewayInteractor(requestCustomer, customerDtoMapper);

        productsDtoMapper = new ProductsDtoMapper();
        productGateway = new ProductGatewayInteractor(requestProducts, productsDtoMapper);

        orderGateway = new OrderRepositoryGateway(orderRepository, orderEntityMapper);

        orderMapper = new OrderMapper();
        orderUseCase = new OrderUseCaseInteractor(orderGateway, customerGateway, productGateway);
        orderApi = new OrderApi(orderUseCase, orderMapper);

        mockMvc = MockMvcBuilders.standaloneSetup(orderApi).setControllerAdvice(new ExceptionHandlerConfig()).build();

        mock = new OrderHelper(orderEntityMapper, customerDtoMapper, productsDtoMapper);

    }

    @Nested
    class TestCheckout {
        @Test
        void testInsertOrderValidateNull() throws Exception {
            OrderRequest request = new OrderRequest(null, "", null);


            Optional<String> jsonRequest = jsonUtils.toJson(request);

            MvcResult mvcResult = mockMvc
                    .perform(post("/api/v1/orders/checkout")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonRequest.orElse(""))).andExpect(status().isBadRequest()).andReturn();

            Optional<Result<OrderResponse>> response = jsonUtils.parse(mvcResult.getResponse().getContentAsString(),
                    new TypeReference<Result<OrderResponse>>() {
                    });

            int code = response.get().getCode();
            assertEquals(400, code, "Must Be Equals");
            assertEquals(List.of("Products is required!"), response.get().getErrors(), "Must Be Equals");
        }


        @Test
        void testInsertOrder_withCustomer_withProducts() throws Exception {
            List<String> skus = List.of("2253001", "2253002");
            String cpf = "37465505569";
            OrderRequest request = new OrderRequest(cpf, skus);

            Order order = new Order().startOrder(  mock.getCustomer(cpf), mock.getProducts(skus));
            OrderDocument orderDocument = orderEntityMapper.toOrderEntity(order);
            when(requestCustomer.findByCpf(cpf)).thenReturn(Result.ok(mock.getCustomerDto(cpf)));
            when(requestProducts.findBySkus(skus)).thenReturn(Result.ok(mock.getProductDtos(skus)));
            when(orderRepository.save(any(OrderDocument.class))).thenReturn(orderDocument);

            Optional<String> jsonRequest = jsonUtils.toJson(request);

            MvcResult mvcResult = mockMvc.perform(post("/api/v1/orders/checkout").contentType(MediaType.APPLICATION_JSON)
                    .content(jsonRequest.orElse(""))).andExpect(status().isCreated()).andReturn();

            Optional<Result<OrderResponse>> response = jsonUtils.parse(mvcResult.getResponse().getContentAsString(),
                    new TypeReference<Result<OrderResponse>>() {
                    });

            OrderResponse orderResponse = response.get().getBody();
            int code = response.get().getCode();
            assertEquals(201, code, "Must Be Equals");
            assertNotNull(orderResponse.custumer(), "Must be not null");
            assertEquals("RECEBIDO", orderResponse.statuOrder(), "Must Be Equals");
            assertEquals(2, orderResponse.products().size(), "Must Be 2 product");

            verify(requestCustomer, times(1)).findByCpf(cpf);
            verify(requestProducts, times(1)).findBySkus(skus);
            verify(orderRepository, times(1)).save(any(OrderDocument.class));
        }

        @Test
        void testInsertOrder_withCustomerNotFound() throws Exception {
            List<String> skus = List.of("2253001", "2253002");
            String cpf = "37465505569";
            OrderRequest request = new OrderRequest(cpf, skus);

            when(requestCustomer.findByCpf(cpf)).thenReturn(Result.notFound(List.of("Customer not found")));

            Optional<String> jsonRequest = jsonUtils.toJson(request);

            MvcResult mvcResult = mockMvc.perform(post("/api/v1/orders/checkout").contentType(MediaType.APPLICATION_JSON)
                    .content(jsonRequest.orElse(""))).andExpect(status().isNotFound()).andReturn();

            Optional<Result> response = jsonUtils.parse(mvcResult.getResponse().getContentAsString(),
                    new TypeReference<Result>() {
                    });

            int code = response.get().getCode();
            assertEquals(404, code, "Must Be Equals");
            assertEquals(List.of("Customer not found for cpf: 37465505569"), response.get().getErrors(), "Must Be Equals");

            verify(requestCustomer, times(1)).findByCpf(cpf);
            verify(requestProducts, never()).findBySkus(skus);
            verify(orderRepository, never()).save(any(OrderDocument.class));
        }

        @Test
        void testInsertOrder_withErrorCustomerApi() throws Exception {
            List<String> skus = List.of("2253001", "2253002");
            String cpf = "37465505569";
            OrderRequest request = new OrderRequest(cpf, skus);

            when(requestCustomer.findByCpf(cpf)).thenThrow(FeignException.class);

            Optional<String> jsonRequest = jsonUtils.toJson(request);

            MvcResult mvcResult = mockMvc.perform(post("/api/v1/orders/checkout").contentType(MediaType.APPLICATION_JSON)
                    .content(jsonRequest.orElse(""))).andExpect(status().isBadRequest()).andReturn();

            Optional<Result> response = jsonUtils.parse(mvcResult.getResponse().getContentAsString(),
                    new TypeReference<Result>() {
                    });

            int code = response.get().getCode();
            assertEquals(400, code, "Must Be Equals");
            assertEquals(List.of("Fail request to customer api"), response.get().getErrors(), "Must Be Equals");

            verify(requestCustomer, times(1)).findByCpf(cpf);
            verify(requestProducts, never()).findBySkus(skus);
            verify(orderRepository, never()).save(any(OrderDocument.class));
        }

        @Test
        void testInsertOrder_withNoCustomer_withProducts() throws Exception {
            List<String> skus = List.of("2253001", "2253002");
            String cpf = null;
            OrderRequest request = new OrderRequest(cpf, skus);

            Order order = new Order().startOrder(null, mock.getProducts(skus));
            OrderDocument orderDocument = orderEntityMapper.toOrderEntity(order);

            when(requestProducts.findBySkus(skus)).thenReturn(Result.ok(mock.getProductDtos(skus)));
            when(orderRepository.save(any(OrderDocument.class))).thenReturn(orderDocument);

            Optional<String> jsonRequest = jsonUtils.toJson(request);

            MvcResult mvcResult = mockMvc.perform(post("/api/v1/orders/checkout").contentType(MediaType.APPLICATION_JSON)
                    .content(jsonRequest.orElse(""))).andExpect(status().isCreated()).andReturn();

            Optional<Result<OrderResponse>> response = jsonUtils.parse(mvcResult.getResponse().getContentAsString(),
                    new TypeReference<Result<OrderResponse>>() {
                    });

            OrderResponse orderResponse = response.get().getBody();
            int code = response.get().getCode();
            assertEquals(201, code, "Must Be Equals");
            assertNull(orderResponse.custumer(), "Must Be Null");
            assertEquals(2, orderResponse.products().size(), "Must Be 2 product");

            verify(requestCustomer, never()).findByCpf(cpf);
            verify(requestProducts, times(1)).findBySkus(skus);
            verify(orderRepository, times(1)).save(any(OrderDocument.class));
        }

        @Test
        void testInsertOrder_withCustomer_withProductsNotFound() throws Exception {
            List<String> skus = List.of("2253001", "2253002");
            String cpf = "37465505569";
            OrderRequest request = new OrderRequest(cpf, skus);

            when(requestCustomer.findByCpf(cpf)).thenReturn(Result.ok(mock.getCustomerDto(cpf)));
            when(requestProducts.findBySkus(skus)).thenReturn(Result.notFound(List.of("")));

            Optional<String> jsonRequest = jsonUtils.toJson(request);

            MvcResult mvcResult = mockMvc.perform(post("/api/v1/orders/checkout").contentType(MediaType.APPLICATION_JSON)
                    .content(jsonRequest.orElse(""))).andExpect(status().isNotFound()).andReturn();

            Optional<Result<OrderResponse>> response = jsonUtils.parse(mvcResult.getResponse().getContentAsString(),
                    new TypeReference<Result<OrderResponse>>() {
                    });

            OrderResponse orderResponse = response.get().getBody();
            int code = response.get().getCode();
            assertEquals(404, code, "Must Be Equals");
            assertEquals(List.of("Any product found!"), response.get().getErrors(), "Must Be Equals");

            verify(requestCustomer, times(1)).findByCpf(cpf);
            verify(requestProducts, times(1)).findBySkus(skus);
            verify(orderRepository, never()).save(any(OrderDocument.class));
        }

        @Test
        void testInsertOrder_withCustomer_withProductsApiError() throws Exception {
            List<String> skus = List.of("2253001", "2253002");
            String cpf = "37465505569";
            OrderRequest request = new OrderRequest(cpf, skus);

            when(requestCustomer.findByCpf(cpf)).thenReturn(Result.ok(mock.getCustomerDto(cpf)));
            when(requestProducts.findBySkus(skus)).thenThrow(FeignException.class);

            Optional<String> jsonRequest = jsonUtils.toJson(request);

            MvcResult mvcResult = mockMvc.perform(post("/api/v1/orders/checkout").contentType(MediaType.APPLICATION_JSON)
                    .content(jsonRequest.orElse(""))).andExpect(status().isBadRequest()).andReturn();

            Optional<Result<OrderResponse>> response = jsonUtils.parse(mvcResult.getResponse().getContentAsString(),
                    new TypeReference<Result<OrderResponse>>() {
                    });

            OrderResponse orderResponse = response.get().getBody();
            int code = response.get().getCode();
            assertEquals(400, code, "Must Be Equals");
            assertEquals(List.of("Fail request to products api"), response.get().getErrors(), "Must Be Equals");

            verify(requestCustomer, times(1)).findByCpf(cpf);
            verify(requestProducts, times(1)).findBySkus(skus);
            verify(orderRepository, never()).save(any(OrderDocument.class));
        }
    }

    @Nested
    class TestFindOrders {
        @Test
        void testFindByid_withSuccess() throws Exception {
            List<String> skus = List.of("2253001", "2253002");
            String cpf = "37465505569";
            String orderId = "852466";
            Order order = new Order().startOrder(mock.getCustomer(cpf), mock.getProducts(skus));

            OrderDocument orderDocument = orderEntityMapper.toOrderEntity(order);
            orderDocument.setId(orderId);
            when(orderRepository.findById(orderId)).thenReturn(Optional.of(orderDocument));
            MvcResult mvcResult = mockMvc.perform(get("/api/v1/orders/find/" + orderId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk()).andReturn();

            Optional<Result<OrderResponse>> response = jsonUtils.parse(mvcResult.getResponse().getContentAsString(),
                    new TypeReference<Result<OrderResponse>>() {
                    });

            OrderResponse orderResponse = response.get().getBody();
            int code = response.get().getCode();
            assertEquals(200, code, "Must Be Equals");
            assertNotNull(orderResponse.custumer(), "Must be not null");
            assertEquals("RECEBIDO", orderResponse.statuOrder(), "Must Be Equals");
            assertEquals(2, orderResponse.products().size(), "Must Be 2 product");

            verify(orderRepository, times(1)).findById(orderId);
        }

        @Test
        void testFindByid_notfound() throws Exception {
            String orderId = "852466";

            when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

            MvcResult mvcResult = mockMvc.perform(get("/api/v1/orders/find/" + orderId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound()).andReturn();

            Optional<Result> response = jsonUtils.parse(mvcResult.getResponse().getContentAsString(),
                    new TypeReference<Result>() {
                    });


            int code = response.get().getCode();
            assertEquals(404, code, "Must Be Equals");
            assertEquals("Order not found!", response.get().getErrors().get(0), "Must Be Equals");
            verify(orderRepository, times(1)).findById(orderId);
        }


        @Test
        void testFindAll_withPageable_nextFalse() throws Exception {
            String orderId = "852466";
            List<String> skus = List.of("2253001", "2253002");
            String cpf = "37465505569";
            Order order = new Order().startOrder(mock.getCustomer(cpf), mock.getProducts(skus));
            Pageable pageable = Pageable.ofSize(5).withPage(0);
            OrderDocument orderDocument = orderEntityMapper.toOrderEntity(order);
            orderDocument.setId(orderId);
            Page<OrderDocument> page = new PageImpl<>(List.of(orderDocument));

            when(orderRepository.findAll(any(Pageable.class))).thenReturn(page);

            MvcResult mvcResult = mockMvc.perform(get("/api/v1/orders?page=0&size=5")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk()).andReturn();

            Optional<Result<List<OrderResponse>>> response = jsonUtils.parse(mvcResult.getResponse().getContentAsString(),
                    new TypeReference<Result<List<OrderResponse>>>() {
                    });

            int code = response.get().getCode();
            assertEquals(200, code, "Must Be Equals");
            assertEquals(1, response.get().getTotal(), "Must Be Equals");
            assertFalse(response.get().getHasNext());
        }

        @Test
        void testFindByOrderAndDate() throws Exception {
            String orderId = "852466";
            List<String> skus = List.of("2253001", "2253002");
            String cpf = "37465505569";
            Order order = new Order().startOrder(mock.getCustomer(cpf), mock.getProducts(skus));
            Pageable pageable = Pageable.ofSize(5).withPage(0);
            OrderDocument orderDocument = orderEntityMapper.toOrderEntity(order);
            orderDocument.setId(orderId);

            when(orderRepository.findByStatusOrderAndDateOrderInit(any(Sort.class))).thenReturn(List.of(orderDocument));

            MvcResult mvcResult = mockMvc.perform(get("/api/v1/orders/sorted")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk()).andReturn();

            Optional<Result<List<OrderResponse>>> response = jsonUtils.parse(mvcResult.getResponse().getContentAsString(),
                    new TypeReference<Result<List<OrderResponse>>>() {
                    });

            int code = response.get().getCode();
            assertEquals(200, code, "Must Be Equals");
            assertEquals(1, response.get().getBody().size(), "Must Be Equals");
        }
    }

}