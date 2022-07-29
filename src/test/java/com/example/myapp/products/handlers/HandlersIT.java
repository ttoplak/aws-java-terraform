package com.example.myapp.products.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.services.lambda.runtime.tests.EventLoader;
import com.example.myapp.products.data.Product;
import com.example.myapp.products.exceptions.InvalidObjectException;
import com.example.myapp.products.services.ProductService;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;


public class HandlersIT {

    public static ProductService productService;

    public static DynamoDbClient ddb;

    @Mock
    public Context contextMock;

    @Mock
    public LambdaLogger loggerMock;


    @BeforeAll
    public static void setUp() {
        DockerImageName localstackImage = DockerImageName.parse("localstack/localstack:0.11.3");
        LocalStackContainer localstack = new LocalStackContainer(localstackImage)
                .withServices(
                        LocalStackContainer.Service.DYNAMODB,
                        LocalStackContainer.Service.LAMBDA
                );
        localstack.start();

        ddb = DynamoDbClient
                .builder()
                .endpointOverride(localstack.getEndpointOverride(LocalStackContainer.Service.DYNAMODB))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(localstack.getAccessKey(), localstack.getSecretKey())
                        )
                )
                .region(Region.of(localstack.getRegion()))
                .build();

        productService = new ProductService(ddb);
    }

    @BeforeEach
    public void create() {
        ddb.createTable(
                CreateTableRequest
                        .builder()
                        .tableName("Products")
                        .billingMode(BillingMode.PAY_PER_REQUEST)
                        .attributeDefinitions(
                                AttributeDefinition
                                        .builder()
                                        .attributeName("ProductId")
                                        .attributeType(ScalarAttributeType.S)
                                        .build()
                        )
                        .keySchema(
                                KeySchemaElement
                                        .builder()
                                        .attributeName("ProductId")
                                        .keyType(KeyType.HASH)
                                        .build()
                        )
                        .build()
        );

        createProducts().forEach(p -> {
            try {
                productService.createProduct(p);
            } catch (InvalidObjectException e) {
                throw new RuntimeException(e);
            }
        });

        MockitoAnnotations.openMocks(this);

        when(contextMock.getLogger()).thenReturn(loggerMock);
    }

    private List<Product> createProducts() {
        return Arrays.asList(
                new Product("id-1", "name-1", 1, "www.test-url-1.com"),
                new Product("id-2", "name-2", 10, "www.test-url-2.com"),
                new Product("id-3", "name-3", 15, "www.test-url-3.com"),
                new Product("id-4", "name-4",100, "www.test-url-4.com")
        );
    }

    @AfterEach
    public void teardown() {
        ddb.deleteTable(DeleteTableRequest.builder().tableName("Products").build());
    }

    @Test
    public void test() {
        APIGatewayProxyRequestEvent event = EventLoader.loadApiGatewayRestEvent("products/get/events/normal_get_request.json");
        GetHandler handler = new GetHandler(productService);

        APIGatewayProxyResponseEvent result = handler.handleRequest(event, contextMock);

        Assertions.assertEquals(200, result.getStatusCode());
        Assertions.assertEquals(handler.gson.toJson(createProducts()), result.getBody());
    }
}
