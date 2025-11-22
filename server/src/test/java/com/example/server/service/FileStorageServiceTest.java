package com.example.server.service;

import com.cloudinary.Cloudinary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class FileStorageServiceTest {
    @Mock private MultipartFile multipartFile;
    @TempDir Path tempDir;
    private FileStorageService fileStorageService;

    @BeforeEach
    void setup() throws IOException {
        MockitoAnnotations.openMocks(this);
        String uploadDir = tempDir.toString();
        fileStorageService = new FileStorageService(
            uploadDir,
            "http://localhost:8080",
            false,
            "test-folder",
            null
        );
    }

    @Test
    void storeProjectImage_ReturnsUrl() throws IOException {
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getContentType()).thenReturn("image/jpeg");
        when(multipartFile.getSize()).thenReturn(1024L);
        when(multipartFile.getOriginalFilename()).thenReturn("test.jpg");
        when(multipartFile.getInputStream()).thenReturn(new java.io.ByteArrayInputStream(new byte[1024]));
        String result = fileStorageService.storeProjectImage(multipartFile);
        assertThat(result).isNotNull();
        assertThat(result).contains("/uploads/projects/");
    }

    @Test
    void storeProjectImage_EmptyFile_ThrowsException() {
        when(multipartFile.isEmpty()).thenReturn(true);
        assertThatThrownBy(() -> fileStorageService.storeProjectImage(multipartFile))
            .isInstanceOf(IOException.class)
            .hasMessageContaining("File rỗng");
    }

    @Test
    void storeProjectImage_InvalidContentType_ThrowsException() {
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getContentType()).thenReturn("application/pdf");
        assertThatThrownBy(() -> fileStorageService.storeProjectImage(multipartFile))
            .isInstanceOf(IOException.class)
            .hasMessageContaining("Chỉ chấp nhận file ảnh");
    }

    @Test
    void storeProjectImage_FileTooLarge_ThrowsException() {
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getContentType()).thenReturn("image/jpeg");
        when(multipartFile.getSize()).thenReturn(6 * 1024 * 1024L);
        assertThatThrownBy(() -> fileStorageService.storeProjectImage(multipartFile))
            .isInstanceOf(IOException.class)
            .hasMessageContaining("5MB");
    }

    @Test
    void storeUserAvatar_ReturnsUrl() throws IOException {
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getContentType()).thenReturn("image/png");
        when(multipartFile.getSize()).thenReturn(2048L);
        when(multipartFile.getOriginalFilename()).thenReturn("avatar.png");
        when(multipartFile.getInputStream()).thenReturn(new java.io.ByteArrayInputStream(new byte[2048]));
        String result = fileStorageService.storeUserAvatar(multipartFile);
        assertThat(result).isNotNull();
        assertThat(result).contains("/uploads/avatars/");
    }

}

