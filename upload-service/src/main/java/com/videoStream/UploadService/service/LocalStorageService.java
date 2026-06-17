package com.videoStream.UploadService.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class LocalStorageService implements VideoStorageService {

    @Value("${storage.local.path}")
    private String localPath;

    @Override
    public String storeVideo(MultipartFile file) throws IOException {
        // Ensure upload directory exists
        Path uploadDirPath = Paths.get(localPath);
        if (!Files.exists(uploadDirPath)) {
            Files.createDirectories(uploadDirPath);
        }

        // Generate a unique file name to avoid collisions
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path filePath = uploadDirPath.resolve(fileName);

        // Save file bytes to the local directory
        Files.write(filePath, file.getBytes());

        // Return path reference (relative path for streaming/playback service)
        return "/api/v1/stream/" + fileName;
    }
}
