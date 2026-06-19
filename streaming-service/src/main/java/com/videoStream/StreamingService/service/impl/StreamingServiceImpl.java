package com.videoStream.StreamingService.service.impl;

import com.videoStream.StreamingService.exception.VideoNotFoundException;
import com.videoStream.StreamingService.service.StreamingService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

@Service
public class StreamingServiceImpl implements StreamingService {

    @Value("${storage.local.path}")
    private String localPath;

    @Override
    public Resource getVideoResource(String fileName) {
        File videoFile = Paths.get(localPath).resolve(fileName).toFile();
        if (!videoFile.exists()) {
            throw new VideoNotFoundException("Video file not found: " + fileName);
        }
        return new FileSystemResource(videoFile);
    }

    @Override
    public ResourceRegion getVideoRegion(String fileName, HttpHeaders headers) throws IOException {
        Resource resource = getVideoResource(fileName);
        long contentLength = resource.contentLength();
        HttpRange range = headers.getRange().isEmpty() ? null : headers.getRange().get(0);

        if (range != null) {
            long start = range.getRangeStart(contentLength);
            long end = range.getRangeEnd(contentLength);
            // Stream in chunks of 1MB
            long rangeLength = Math.min(1024 * 1024L, end - start + 1);
            return new ResourceRegion(resource, start, rangeLength);
        } else {
            // Default chunk length (1MB) if range header not present
            long rangeLength = Math.min(1024 * 1024L, contentLength);
            return new ResourceRegion(resource, 0, rangeLength);
        }
    }
}
