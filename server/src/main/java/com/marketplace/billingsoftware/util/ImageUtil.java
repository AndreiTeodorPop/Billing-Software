package com.marketplace.billingsoftware.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

@Component
public class ImageUtil {

    @Value("${server.port}")
    private int serverPort;

    @Value("${server.servlet.context-path}")
    private String serverContextPath;

    private static final Logger LOGGER = LogManager.getLogger();

    private static final String HTTP = "http://";

    public static final String LOCAL_HOST = "localhost";

    public String addImageFile(MultipartFile file, String uploadPath) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IOException("Uploaded file is empty or missing");
        }
        Path projectRoot = Paths.get(System.getProperty("user.dir"));
        Path targetDir = projectRoot.resolve("server").resolve(uploadPath).normalize();
        Files.createDirectories(targetDir);

        String originalFilename = Paths.get(Objects.requireNonNull(file.getOriginalFilename())).getFileName().toString();

        Path targetPath = targetDir.resolve(originalFilename);

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
        }

        String fileUrl = String.format("%s%s:%d%s/images/%s", HTTP, LOCAL_HOST, serverPort, serverContextPath, originalFilename);
        System.out.println("✅ Image saved successfully at: " + targetPath);
        System.out.println("🌐 Accessible at: " + fileUrl);

        return fileUrl;
    }

    public boolean deleteImageFile(String imageUrl, String uploadPath) {
        try {
            String filename = Paths.get(new URI(imageUrl).getPath()).getFileName().toString();

            Path projectRoot = Paths.get(System.getProperty("user.dir"));
            Path filePath = projectRoot.resolve("server").resolve(uploadPath + File.separator + filename).normalize();

            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            LOGGER.error(e);
            return false;
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
