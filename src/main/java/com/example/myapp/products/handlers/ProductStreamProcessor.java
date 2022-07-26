package com.example.myapp.products.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.amazonaws.services.lambda.runtime.events.models.dynamodb.AttributeValue;
import com.example.myapp.products.data.Product;
import com.example.myapp.products.services.ImageService;
import com.example.myapp.products.services.ProductService;
import com.example.myapp.products.services.S3Service;
import com.example.myapp.products.services.SQSService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

public class ProductStreamProcessor implements RequestHandler<DynamodbEvent, Void> {

    public static final String RESULTS_QUEUE = "product-stream-processor-results-queue";

    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    ImageService imageService;

    S3Service s3Service;

    SQSService sqsService;

    public ProductStreamProcessor() {
        this.imageService = new ImageService();
        this.s3Service = new S3Service();
        this.sqsService = new SQSService();
    }

    public ProductStreamProcessor(ImageService imageService, S3Service s3Service, SQSService sqsService) {
        this.imageService = imageService;
        this.s3Service = s3Service;
        this.sqsService = sqsService;
    }

    @Override
    public Void handleRequest(DynamodbEvent dynamodbEvent, Context context) {
        log(dynamodbEvent, context);
        try {
            process(dynamodbEvent);
        } catch (IOException exception) {
            context.getLogger().log("Error processing image: " + exception.getMessage());
        }
        return null;
    }

    private void log(DynamodbEvent dynamodbEvent, Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log("Event: " + gson.toJson(dynamodbEvent));
    }

    private void process(DynamodbEvent dynamodbEvent) throws IOException {
        Product newProduct = extractProduct(dynamodbEvent);
        ImageService.Image image = imageService.downloadImage(newProduct.getPictureURL());
        s3Service.uploadImageToS3(image.imageStream, image.contentLength, image.URL);
        sqsService.sendMessage(RESULTS_QUEUE, gson.toJson(newProduct));
    }

    private Product extractProduct(DynamodbEvent dynamodbEvent) {
        DynamodbEvent.DynamodbStreamRecord record = dynamodbEvent.getRecords().get(0);
        Map<String, AttributeValue> newImage = record.getDynamodb().getNewImage();
        return Product.fromImage(newImage);
    }
}
