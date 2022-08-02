package com.example.myapp.products.services;

import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SqsException;

public class SQSService {

    SqsClient sqsClient;

    public SQSService () {
        sqsClient = SqsClient.builder().build();
    }

    public String getQueueURL(String queueName) {
        return sqsClient.getQueueUrl(GetQueueUrlRequest.builder().queueName(queueName).build()).queueUrl();
    }

    public void sendMessage(String queueName, String body) throws SqsException {
        String queueURL = getQueueURL(queueName);
        sqsClient.sendMessage(
                SendMessageRequest
                        .builder()
                        .queueUrl(queueURL)
                        .messageBody(body)
                        .build()
        );
    }
}
