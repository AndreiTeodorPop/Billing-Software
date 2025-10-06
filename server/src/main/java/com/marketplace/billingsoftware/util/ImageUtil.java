package com.marketplace.billingsoftware.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ImageUtil {

    private static final Logger LOGGER = LogManager.getLogger();

    public static boolean deleteImageFile(String imageUrl, String uploadPath) {
        try {
            String filename = Paths.get(new URI(imageUrl).getPath()).getFileName().toString();
            Path filePath = Paths.get(uploadPath + File.separator + filename);

            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            LOGGER.error(e);
            return false;
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
