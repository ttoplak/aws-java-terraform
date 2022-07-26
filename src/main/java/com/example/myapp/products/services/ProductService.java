package com.example.myapp.products.services;

import com.example.myapp.products.data.Product;
import org.joda.time.DateTime;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

import java.sql.Time;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class ProductService {

    private static final String PRODUCT_TABLE = "Products";

    private static DynamoDbTable<Product> getProductTable() throws DynamoDbException {
        DynamoDbClient ddb = DynamoDbClient.builder().build();
        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(ddb).build();
        return enhancedClient.table(PRODUCT_TABLE, TableSchema.fromBean(Product.class));
    }

    public static List<Product> getProducts() throws DynamoDbException {
        return getProductTable()
                .scan()
                .items()
                .stream()
                .collect(Collectors.toList());
    }

    public static Product createProduct(Product product) throws DynamoDbException {
        product.setProductID(String.format("%s-%s", DateTime.now(), new Random().nextInt()));
        return getProductTable().updateItem(product);
    }

    public static Product updateProduct(String productID, Product updatedProduct) throws DynamoDbException {
        updatedProduct.setProductID(productID);
        return getProductTable().updateItem(updatedProduct);
    }
}
