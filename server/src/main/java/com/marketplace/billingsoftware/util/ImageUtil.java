package com.marketplace.billingsoftware.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ImageUtil {

    private static final Logger LOGGER = LogManager.getLogger();

    public static String addImageFile(MultipartFile file, String uploadPath) throws IOException {
        String filename = file.getOriginalFilename();
        Path imagePath = Paths.get(uploadPath + File.separator + filename);
        Files.createDirectories(imagePath.getParent());
        Files.write(imagePath, file.getBytes());
        return "http://localhost:8080/api/v1.0/images/" + filename;
    }

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
