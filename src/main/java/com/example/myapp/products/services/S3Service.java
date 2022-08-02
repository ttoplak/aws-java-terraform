package com.example.myapp.products.services;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.InputStream;

public class S3Service {

    public void uploadImageToS3(InputStream imageStream, long contentLength, String pictureURL) {
        S3Client s3 = S3Client.builder().build();
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket("toplak-playground-bucket")
                .key(pictureURL)
                .acl(ObjectCannedACL.PUBLIC_READ)
                .build();
        s3.putObject(request, RequestBody.fromInputStream(imageStream, contentLength));
    }
}
