package com.videoStream.UploadService.controller;

import com.videoStream.UploadService.service.VideoStorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/upload")
public class UploadController {

    private final VideoStorageService storageService;

    public UploadController(VideoStorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> uploadVideo(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "description", required = false) String description) {
        
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("status", 1, "error", "File is empty"));
        }

        try {
            String fileUrl = storageService.storeVideo(file);
            Map<String, Object> response = new HashMap<>();
            response.put("status", 0);
            response.put("videoUrl", fileUrl);
            response.put("title", title);
            response.put("description", description);
            
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(Map.of("status", 1, "error", "Failed to store file: " + e.getMessage()));
        }
    }
}
