package com.techchallenge.bdd;



import com.techchallenge.core.response.JsonUtils;
import com.techchallenge.core.response.ObjectMapperConfig;
import com.techchallenge.infrastructure.api.request.OrderRequest;
import com.techchallenge.infrastructure.external.dtos.CustomerDto;
import com.techchallenge.infrastructure.external.dtos.ProductDto;
import com.techchallenge.utils.OrderHelper;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Optional;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.collection.IsMapContaining.hasKey;


public class StepDefinition {

    private Response response;

    OrderHelper helper = new OrderHelper();

    private String ENDPOINT_ORDERS = "http://localhost:8085/api/v1/orders";
    private String ENDPOINT_PRODUCTS = "http://localhost:8084/api/v1/products";
    private String ENDPOINT_CUSTOMERS = "http://localhost:8083/api/v1/customers";

    @Dado("que quero inciar um pedido")
    public void que_quero_inciar_um_pedido() {
        addCustomerAndProducts();
    }
    @Quando("informando cliente e produtos")
    public void informando_cliente_e_produtos() {
        OrderRequest request = new OrderRequest("233", "60186822731", List.of("sku123", "sku125"));
        response = given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when().post(ENDPOINT_ORDERS+"/checkout");
    }
    @Entao("devo conseguir iniciar um pedido")
    public void devo_conseguir_iniciar_um_pedido() {
        response.then()
                .statusCode(HttpStatus.CREATED.value())
                .body(matchesJsonSchemaInClasspath("./data/orders-schema-find.json"));
    }

    @Dado("que quero inciar um pedido sem cliente")
    public void que_quero_inciar_um_pedido_sem_cliente() {
        addCustomerAndProducts();

    }
    @Quando("informando apenas produtos")
    public void informando_apenas_produtos() {
        OrderRequest request = new OrderRequest(null, List.of("sku123", "sku125"));
        response = given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when().post(ENDPOINT_ORDERS+"/checkout");
    }
    @Entao("devo conseguir iniciar um pedido apenas com produto")
    public void devo_conseguir_iniciar_um_pedido_apenas_com_produto() {
        response.then()
                .statusCode(HttpStatus.CREATED.value())
                .body(matchesJsonSchemaInClasspath("./data/orders-scheam-without-customer.json"));;;;

    }

    @Dado("que quero pesquisar um pedido")
    public void que_quero_pesquisar_um_pedido() {
        addCustomerAndProducts();
        OrderRequest request = new OrderRequest( "order123","60186822731", List.of("sku123", "sku125"));
        given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when().post(ENDPOINT_ORDERS+"/checkout").then()
                .statusCode(HttpStatus.CREATED.value());

    }
    @Quando("informar um id")
    public void informar_um_id() {
        response = given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get(ENDPOINT_ORDERS+"/find/{id}", "order123" );
    }
    @Entao("devo conseguir obter um pedido")
    public void devo_conseguir_obter_um_pedido() {
        response.then()
                .statusCode(HttpStatus.OK.value())
                .body(matchesJsonSchemaInClasspath("./data/orders-schema-find.json"));;
    }

    @Dado("que pesquiso por todos os pedidos")
    public void que_pesquiso_por_todos_os_pedidos() {
        response = given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get(ENDPOINT_ORDERS);
    }
    @Entao("devo conseguir obter todos os pedido")
    public void devo_conseguir_obter_todos_os_pedido() {
        response.then()
                .statusCode(HttpStatus.OK.value())
                .body(matchesJsonSchemaInClasspath("./data/orders-schema-list.json"));;
    }

    @Dado("que preciso da lista de pedidos ordenada")
    public void que_preciso_da_lista_de_pedidos_ordenada() {
        response = given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get(ENDPOINT_ORDERS+"/sorted");
    }
    @Entao("devo conseguir obter a lista de pedidos ordenada por data")
    public void devo_conseguir_obter_a_lista_de_pedidos_ordenada_por_data() {
        response.then()
                .statusCode(HttpStatus.OK.value())
                .body(matchesJsonSchemaInClasspath("./data/orders-schema-sorted.json"));
    }






    public void  addCustomerAndProducts(){
        JsonUtils jsonUtils = new JsonUtils(new ObjectMapperConfig().objectMapper());
        CustomerDto customerDto = helper.getCustomerDto("60186822731");
        Optional<String> requestCustomer = jsonUtils.toJson(customerDto);

        ProductDto productDto1 = helper.getProductDto("sku123");
        ProductDto productDto2 = helper.getProductDto("sku125");

        Optional<String> requestProduct1 = jsonUtils.toJson(productDto1);
        Optional<String> requestProduct2 = jsonUtils.toJson(productDto2);

        given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(requestProduct1.orElse(""))
                .when().post(ENDPOINT_PRODUCTS)
                .then()
                .statusCode(HttpStatus.CREATED.value());

        given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(requestProduct2.orElse(""))
                .when().post(ENDPOINT_PRODUCTS)
                .then()
                .statusCode(HttpStatus.CREATED.value());;

        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(requestCustomer.orElse(""))
                .when().post(ENDPOINT_CUSTOMERS)
                .then()
                .statusCode(HttpStatus.CREATED.value());;
    }




}
