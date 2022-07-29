package com.example.myapp.products.handlers;


import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.example.myapp.products.data.Product;
import com.example.myapp.products.exceptions.InvalidObjectException;
import com.example.myapp.products.services.ProductService;
import com.example.myapp.products.utility.Logger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

import java.util.List;

public class PutHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    ProductService productService;

    public PutHandler() {
        this.productService = new ProductService();
    }

    public PutHandler(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {
        logEvent(event, context);
        return processEvent(event);
    }

    private void logEvent(APIGatewayProxyRequestEvent event, Context context) {
        Logger logger = new Logger(context);
        logger.log(event);
    }

    private APIGatewayProxyResponseEvent processEvent(APIGatewayProxyRequestEvent event) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        try {
            String productId = event.getPathParameters().get("productId");
            Product product = new Gson().fromJson(event.getBody(), Product.class);
            product = productService.updateProduct(productId, product);
            response.withStatusCode(200).withBody(gson.toJson(product));
        } catch (DynamoDbException ddbException) {
            response.withStatusCode(500).withBody(ddbException.getMessage());
        } catch (InvalidObjectException objectException) {
            response.withStatusCode(400).withBody(objectException.getMessage());
        }
        return response;
    }
}