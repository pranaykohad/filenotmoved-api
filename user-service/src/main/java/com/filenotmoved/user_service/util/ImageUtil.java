package com.filenotmoved.user_service.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import net.coobird.thumbnailator.Thumbnails;

public class ImageUtil {

    public static byte[] resizeImage(InputStream inputStream, int width, int height) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Thumbnails.of(inputStream)
                .size(width, height)
                .outputFormat("jpg")
                .outputQuality(0.8)
                .toOutputStream(outputStream);

        return outputStream.toByteArray();
    }

}
