package com.videoStream.UploadService.service;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface VideoStorageService {
    String storeVideo(MultipartFile file) throws IOException;
}
