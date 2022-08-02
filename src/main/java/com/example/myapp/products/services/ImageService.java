package com.example.myapp.products.services;

import com.amazonaws.services.lambda.runtime.Context;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class ImageService {

    public Image downloadImage(String pictureURL) throws IOException {
        URL url = new URL(pictureURL);
        URLConnection conn = url.openConnection();
        return new Image(conn.getInputStream(), conn.getContentLengthLong(), pictureURL);
    }

    public static class Image {
        public InputStream imageStream;
        public long contentLength;
        public String URL;

        public Image(InputStream imageStream, long contentLength, String URL) {
            this.imageStream = imageStream;
            this.contentLength = contentLength;
            this.URL = URL;
        }
    }
}
