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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class PostHandlerTest {

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
    private APIGatewayProxyRequestEvent createProductRequestEvent(Product product) {
        APIGatewayProxyRequestEvent requestEvent = new APIGatewayProxyRequestEvent();
        return requestEvent.withBody(gson.toJson(product));
    }

    @Test
    public void testCreateProduct() throws InvalidObjectException {
        Product product = createProduct();
        when(productService.createProduct(any(Product.class))).thenReturn(product);
        APIGatewayProxyRequestEvent requestEvent = createProductRequestEvent(createProduct());
        PostHandler handler = new PostHandler(productService);

        APIGatewayProxyResponseEvent result = handler.handleRequest(requestEvent, contextMock);

        Assertions.assertEquals(200, result.getStatusCode());
        Assertions.assertEquals(gson.toJson(product), result.getBody());
    }

    @Test
    public void testCreateProductDynamoDBError() throws InvalidObjectException {
        when(productService.createProduct(any(Product.class)))
                .thenThrow(DynamoDbException.builder().message("DynamoDB error").build());
        APIGatewayProxyRequestEvent requestEvent = createProductRequestEvent(createProduct());
        PostHandler handler = new PostHandler(productService);

        APIGatewayProxyResponseEvent result = handler.handleRequest(requestEvent, contextMock);

        Assertions.assertEquals(500, result.getStatusCode());
        Assertions.assertFalse(result.getBody().isEmpty());
    }

    @Test
    public void testCreateProductInvalidError() throws InvalidObjectException {
        when(productService.createProduct(any(Product.class)))
                .thenThrow(new InvalidObjectException("Invalid object"));
        APIGatewayProxyRequestEvent requestEvent = createProductRequestEvent(createProduct());
        PostHandler handler = new PostHandler(productService);

        APIGatewayProxyResponseEvent result = handler.handleRequest(requestEvent, contextMock);

        Assertions.assertEquals(400, result.getStatusCode());
        Assertions.assertFalse(result.getBody().isEmpty());
    }
}
