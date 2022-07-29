package com.example.myapp.products.services;

import com.example.myapp.products.data.Product;
import com.example.myapp.products.exceptions.InvalidObjectException;
import org.joda.time.DateTime;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

import java.sql.Time;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

public class ProductService {

    private static final String PRODUCT_TABLE = "Products";

    private final DynamoDbEnhancedClient client;

    public ProductService() {
        DynamoDbClient ddb = DynamoDbClient.builder().build();
        this.client = DynamoDbEnhancedClient.builder().dynamoDbClient(ddb).build();
    }

    public ProductService(DynamoDbClient ddb) {
        this.client = DynamoDbEnhancedClient.builder().dynamoDbClient(ddb).build();
    }

    private DynamoDbTable<Product> getProductTable() throws DynamoDbException {
        return client.table(PRODUCT_TABLE, TableSchema.fromBean(Product.class));
    }

    public List<Product> getProducts() throws DynamoDbException {
        return getProductTable()
                .scan()
                .items()
                .stream()
                .sorted(Comparator.comparing(Product::getName))
                .collect(Collectors.toList());
    }

    public Product createProduct(Product product) throws DynamoDbException, InvalidObjectException {
        validateProduct(product);
        if (product.getProductID() == null) {
            product.setProductID(String.format("%s-%s", DateTime.now(), new Random().nextInt()));
        }
        return getProductTable().updateItem(product);
    }

    public Product updateProduct(String productID, Product updatedProduct) throws DynamoDbException, InvalidObjectException {
        validateProduct(updatedProduct);
        updatedProduct.setProductID(productID);
        return getProductTable().updateItem(updatedProduct);
    }

    private void validateProduct(Product product) throws InvalidObjectException {
        try {
            Objects.requireNonNull(product.getName());
            Objects.requireNonNull(product.getPictureURL());
            Objects.requireNonNull(product.getPrice());
        } catch (NullPointerException exception) {
            throw new InvalidObjectException("Product is missing properties.", exception);
        }
    }
}
