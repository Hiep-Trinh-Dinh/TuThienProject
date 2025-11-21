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
                              @Value("${cloudinary.folder:charity-app}") String cloudinaryFolder,
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
        return storeFile(file, "projects", "project");
    }

    public String storeUserAvatar(MultipartFile file) throws IOException {
        return storeFile(file, "avatars", "avatar");
    }

    public String storeUserCover(MultipartFile file) throws IOException {
        return storeFile(file, "covers", "cover");
    }

    private String storeFile(MultipartFile file, String folder, String prefix) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("File rỗng");
        }

        // Validate file type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IOException("Chỉ chấp nhận file ảnh");
        }

        // Validate file size (5MB)
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IOException("Kích thước file không được vượt quá 5MB");
        }

        String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String extension = "";
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex != -1) {
            extension = originalFilename.substring(dotIndex).toLowerCase();
        }

        String fileName = prefix + "_" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")) +
                extension;

        if (cloudinaryEnabled) {
            return uploadToCloudinary(file, fileName, folder);
        }

        return storeLocally(file, fileName, folder);
    }

    private String uploadToCloudinary(MultipartFile file, String publicId, String folder) throws IOException {
        try {
            Map<?, ?> uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", cloudinaryFolder + "/" + folder,
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
        } catch (Exception e) {
            throw new IOException("Lỗi upload lên Cloudinary: " + e.getMessage());
        }
    }

    private String storeLocally(MultipartFile file, String fileName, String folder) throws IOException {
        Path targetDir = uploadDir.resolve(folder);
        Files.createDirectories(targetDir);

        Path targetLocation = targetDir.resolve(fileName);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        return baseUrl + "/uploads/" + folder + "/" + fileName;
    }

    public boolean deleteFile(String fileUrl) throws IOException {
        if (cloudinaryEnabled && fileUrl.contains("cloudinary")) {
            return deleteFromCloudinary(fileUrl);
        } else {
            return deleteLocalFile(fileUrl);
        }
    }

    private boolean deleteFromCloudinary(String fileUrl) throws IOException {
        try {
            // Extract public_id from Cloudinary URL
            String publicId = extractPublicIdFromUrl(fileUrl);
            Map<?, ?> result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            return "ok".equals(result.get("result"));
        } catch (Exception e) {
            throw new IOException("Lỗi xóa file từ Cloudinary: " + e.getMessage());
        }
    }

    private String extractPublicIdFromUrl(String fileUrl) {
        String[] parts = fileUrl.split("/");
        String publicIdWithExtension = parts[parts.length - 1];
        return publicIdWithExtension.substring(0, publicIdWithExtension.lastIndexOf('.'));
    }

    private boolean deleteLocalFile(String fileUrl) {
        try {
            String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
            String folder = fileUrl.contains("/avatars/") ? "avatars" :
                    fileUrl.contains("/covers/") ? "covers" : "projects";

            Path filePath = uploadDir.resolve(folder).resolve(fileName);
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            return false;
        }
    }
}