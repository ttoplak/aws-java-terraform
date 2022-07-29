package com.example.myapp.products.utility;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Logger {

    private final LambdaLogger lambdaLogger;

    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public Logger(Context context) {
        this.lambdaLogger = context.getLogger();
    }

    public void log(APIGatewayProxyRequestEvent event) {
        lambdaLogger.log("EVENT: " + gson.toJson(event));
        lambdaLogger.log("EVENT TYPE: " + event.getClass().toString());
    }
}
