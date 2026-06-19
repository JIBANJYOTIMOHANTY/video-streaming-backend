package com.videoStream.StreamingService.service;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpHeaders;

import java.io.IOException;

public interface StreamingService {
    Resource getVideoResource(String fileName);
    ResourceRegion getVideoRegion(String fileName, HttpHeaders headers) throws IOException;
}
