package com.example.myapp.products.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.example.myapp.products.data.Product;
import com.example.myapp.products.handlers.GetHandler;
import com.example.myapp.products.services.ProductService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;

public class GetHandlerTest {

    @Mock
    public Context contextMock;
    @Mock
    public LambdaLogger loggerMock;
    @Mock
    public ProductService productService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        when(contextMock.getLogger()).thenReturn(loggerMock);
    }

    private List<Product> createProducts() {
        return Arrays.asList(
                new Product("id-1", "name-1", 1, "www.test-url-1.com"),
                new Product("id-2", "name-2", 10, "www.test-url-2.com"),
                new Product("id-3", "name-3", 15, "www.test-url-3.com"),
                new Product("id-4", "name-4", 100, "www.test-url-4.com")
        );
    }

    @Test
    public void testGetProducts() {
        when(productService.getProducts()).thenReturn(createProducts());
        APIGatewayProxyRequestEvent requestEvent = new APIGatewayProxyRequestEvent();
        GetHandler handler = new GetHandler(productService);

        APIGatewayProxyResponseEvent result = handler.handleRequest(requestEvent, contextMock);

        Assertions.assertEquals(200, result.getStatusCode());
        Assertions.assertEquals(handler.gson.toJson(productService.getProducts()), result.getBody());
    }

    @Test
    public void testGetProductsDynamoDBError() {
        when(productService.getProducts()).thenThrow(DynamoDbException.builder().message("test-exception").build());
        APIGatewayProxyRequestEvent requestEvent = new APIGatewayProxyRequestEvent();
        GetHandler handler = new GetHandler(productService);

        APIGatewayProxyResponseEvent result = handler.handleRequest(requestEvent, contextMock);

        Assertions.assertEquals(500, result.getStatusCode());
        Assertions.assertEquals("test-exception", result.getBody());
    }
}
