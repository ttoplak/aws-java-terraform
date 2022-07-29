package com.example.myapp.products.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.amazonaws.services.lambda.runtime.events.models.dynamodb.AttributeValue;
import com.example.myapp.products.data.Product;
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

public class ProductStreamProcessor implements RequestHandler<DynamodbEvent, String> {

    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public String handleRequest(DynamodbEvent dynamodbEvent, Context context) {
        log(dynamodbEvent, context);
        process(dynamodbEvent, context);
        return "wow";
    }

    private void log(DynamodbEvent dynamodbEvent, Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log("Event: " + gson.toJson(dynamodbEvent));
    }

    private void process(DynamodbEvent dynamodbEvent, Context context) {
        DynamodbEvent.DynamodbStreamRecord record = dynamodbEvent.getRecords().get(0);
        Map<String, AttributeValue> newImage = record.getDynamodb().getNewImage();
        String pictureURL = newImage.get("PictureURL").getS();
        context.getLogger().log("PictureURL: " + gson.toJson(pictureURL));
        downloadImage(pictureURL, context);
    }

    private void downloadImage(String pictureURL, Context context) {
        try {
            URL url = new URL(pictureURL);
            URLConnection conn = url.openConnection();
            uploadImageToS3(conn.getInputStream(), conn.getContentLengthLong(), pictureURL);
        } catch (IOException e) {
            context.getLogger().log("ERROR: " + e.getMessage());
        }
    }

    private void uploadImageToS3(InputStream imageStream, long contentLength, String pictureURL) {
        S3Client s3 = S3Client.builder().build();
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket("toplak-playground-bucket")
                .key(pictureURL)
                .acl(ObjectCannedACL.PUBLIC_READ)
                .build();
        s3.putObject(request, RequestBody.fromInputStream(imageStream, contentLength));
    }
}
