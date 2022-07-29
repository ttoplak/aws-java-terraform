package com.example.myapp.products.data;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@DynamoDbBean
public class Product {
    private String productID;
    private String name;
    private Integer price;
    private String pictureURL;

    public Product() {}

    public Product(String productID, String name, Integer price, String pictureURL) {
        this.productID = productID;
        this.name = name;
        this.price = price;
        this.pictureURL = pictureURL;
    }

    public Product(String name, Integer price, String pictureURL) {
        this.name = name;
        this.price = price;
        this.pictureURL = pictureURL;
    }

    @DynamoDbPartitionKey
    @DynamoDbAttribute("ProductId")
    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    @DynamoDbAttribute("Name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @DynamoDbAttribute("Price")
    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    @DynamoDbAttribute("PictureURL")
    public String getPictureURL() {
        return pictureURL;
    }

    public void setPictureURL(String pictureURL) {
        this.pictureURL = pictureURL;
    }

    @Override
    public String toString() {
        return "Product{" +
                "productID='" + productID + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", pictureURL='" + pictureURL + '\'' +
                '}';
    }
}
