package com.videoStream.UploadService.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@ConditionalOnProperty(name = "storage.mode", havingValue = "cloudinary")
public class CloudinaryStorageService implements VideoStorageService {

    private static final Logger logger = LoggerFactory.getLogger(CloudinaryStorageService.class);
    private final Cloudinary cloudinary;

    public CloudinaryStorageService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    @Override
    public String storeVideo(MultipartFile file) throws IOException {
        logger.info("Uploading video to Cloudinary: {}", file.getOriginalFilename());
        if (cloudinary == null) {
            throw new IOException("Cloudinary client is not initialized. Please verify your credentials.");
        }

        try {
            Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                    "resource_type", "video",
                    "folder", "video_stream/videos",
                    "chunk_size", 6000000 // 6MB chunk size for large videos
            ));
            
            String url = (String) uploadResult.get("secure_url");
            logger.info("Video successfully uploaded. URL: {}", url);
            return url;
        } catch (Exception e) {
            logger.error("Failed to upload video to Cloudinary", e);
            throw new IOException("Cloudinary upload failed: " + e.getMessage(), e);
        }
    }

    @Override
    public String storeThumbnail(MultipartFile file) throws IOException {
        logger.info("Uploading thumbnail to Cloudinary: {}", file.getOriginalFilename());
        if (cloudinary == null) {
            throw new IOException("Cloudinary client is not initialized. Please verify your credentials.");
        }

        try {
            Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                    "resource_type", "image",
                    "folder", "video_stream/thumbnails"
            ));

            String url = (String) uploadResult.get("secure_url");
            logger.info("Thumbnail successfully uploaded. URL: {}", url);
            return url;
        } catch (Exception e) {
            logger.error("Failed to upload thumbnail to Cloudinary", e);
            throw new IOException("Cloudinary upload failed: " + e.getMessage(), e);
        }
    }
}
