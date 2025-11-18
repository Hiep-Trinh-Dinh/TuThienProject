package com.example.server.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Objects;

@Service
public class FileStorageService {

    private final Path uploadDir;
    private final String baseUrl;
    private final boolean cloudinaryEnabled;
    private final String cloudinaryFolder;
    private final Cloudinary cloudinary;

    public FileStorageService(@Value("${file.upload-dir:uploads}") String uploadDir,
                              @Value("${file.base-url:http://localhost:8080}") String baseUrl,
                              @Value("${cloudinary.enabled:false}") boolean cloudinaryEnabled,
                              @Value("${cloudinary.folder:projects}") String cloudinaryFolder,
                              @Autowired(required = false) Cloudinary cloudinary) throws IOException {
        this.uploadDir = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(this.uploadDir);
        if (baseUrl.endsWith("/")) {
            this.baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        } else {
            this.baseUrl = baseUrl;
        }
        this.cloudinaryEnabled = cloudinaryEnabled && cloudinary != null;
        this.cloudinaryFolder = cloudinaryFolder;
        this.cloudinary = cloudinary;
    }

    public String storeProjectImage(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("File rỗng");
        }

        String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String extension = "";
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex != -1) {
            extension = originalFilename.substring(dotIndex);
        }
        String fileName = "project_" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));

        if (cloudinaryEnabled) {
            return uploadToCloudinary(file, fileName);
        }

        return storeLocally(file, fileName + extension);
    }

    private String uploadToCloudinary(MultipartFile file, String publicId) throws IOException {
        Map<?, ?> uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "folder", cloudinaryFolder,
                        "public_id", publicId,
                        "overwrite", true,
                        "resource_type", "image"
                )
        );
        Object secureUrl = uploadResult.get("secure_url");
        if (secureUrl == null) {
            throw new IOException("Cloudinary không trả về secure_url");
        }
        return secureUrl.toString();
    }

    private String storeLocally(MultipartFile file, String fileName) throws IOException {
        Path projectDir = uploadDir.resolve("projects");
        Files.createDirectories(projectDir);

        Path targetLocation = projectDir.resolve(fileName);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        return baseUrl + "/uploads/projects/" + fileName;
    }
}

