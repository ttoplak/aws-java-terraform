package com.example.myapp.products.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.example.myapp.products.data.Product;
import com.example.myapp.products.exceptions.InvalidObjectException;
import com.example.myapp.products.services.ProductService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class PutHandlerTest {

    @Mock
    Context contextMock;
    @Mock
    LambdaLogger loggerMock;
    @Mock
    ProductService productService;
    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        when(contextMock.getLogger()).thenReturn(loggerMock);
    }


    private Product createProduct() {
        Product product = new Product();
        product.setPrice(10);
        product.setName("test-product");
        product.setPictureURL("www.test-product.com");
        return product;
    }

    private APIGatewayProxyRequestEvent putProductRequestEvent(Product product) {
        APIGatewayProxyRequestEvent requestEvent = new APIGatewayProxyRequestEvent();
        Map<String, String> pathParameters = new HashMap<String, String>() {{
            put("productId", "test-ID");
        }};

        return requestEvent
                .withPathParameters(pathParameters)
                .withBody(gson.toJson(product));
    }

    @Test
    public void testPutProduct() throws InvalidObjectException {
        Product product = createProduct();
        when(productService.updateProduct(any(String.class), any(Product.class))).thenReturn(product);
        APIGatewayProxyRequestEvent requestEvent = putProductRequestEvent(product);
        PutHandler handler = new PutHandler(productService);

        APIGatewayProxyResponseEvent result = handler.handleRequest(requestEvent, contextMock);

        Assertions.assertEquals(200, result.getStatusCode());
        Assertions.assertEquals(gson.toJson(product), result.getBody());
    }

    @Test
    public void testPutProductDynamoDBError() throws InvalidObjectException {
        when(productService.updateProduct(any(String.class), any(Product.class)))
                .thenThrow(DynamoDbException.builder().message("DynamoDB error").build());
        APIGatewayProxyRequestEvent requestEvent = putProductRequestEvent(createProduct());
        PutHandler handler = new PutHandler(productService);

        APIGatewayProxyResponseEvent result = handler.handleRequest(requestEvent, contextMock);

        Assertions.assertEquals(500, result.getStatusCode());
        Assertions.assertFalse(result.getBody().isEmpty());
    }

    @Test
    public void testPutProductInvalidError() throws InvalidObjectException {
        when(productService.updateProduct(any(String.class), any(Product.class)))
                .thenThrow(new InvalidObjectException("Invalid object"));
        APIGatewayProxyRequestEvent requestEvent = putProductRequestEvent(createProduct());
        PutHandler handler = new PutHandler(productService);

        APIGatewayProxyResponseEvent result = handler.handleRequest(requestEvent, contextMock);

        Assertions.assertEquals(400, result.getStatusCode());
        Assertions.assertFalse(result.getBody().isEmpty());
    }
}
