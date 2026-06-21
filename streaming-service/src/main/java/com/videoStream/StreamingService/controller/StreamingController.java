package com.videoStream.StreamingService.controller;

import com.videoStream.StreamingService.service.StreamingService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/stream")
public class StreamingController {

    private final StreamingService streamingService;

    public StreamingController(StreamingService streamingService) {
        this.streamingService = streamingService;
    }

    @GetMapping("/thumbnail/{fileName:.+}")
    public ResponseEntity<Resource> getThumbnail(@PathVariable String fileName) {
        Resource resource = streamingService.getThumbnailResource(fileName);
        MediaType mediaType = MediaTypeFactory.getMediaType(resource)
                .orElse(MediaType.IMAGE_PNG);
        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(resource);
    }

    @GetMapping("/{fileName:.+}")
    public ResponseEntity<ResourceRegion> streamVideo(
            @PathVariable String fileName,
            @RequestHeader HttpHeaders headers) throws IOException {

        Resource videoResource = streamingService.getVideoResource(fileName);
        MediaType mediaType = MediaTypeFactory.getMediaType(videoResource)
                .orElse(MediaType.parseMediaType("video/mp4"));

        if (headers.getRange().isEmpty()) {
            long contentLength = videoResource.contentLength();
            ResourceRegion region = new ResourceRegion(videoResource, 0, contentLength);
            return ResponseEntity.ok()
                    .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                    .contentType(mediaType)
                    .body(region);
        }

        ResourceRegion region = streamingService.getVideoRegion(fileName, headers);

        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                .contentType(mediaType)
                .body(region);
    }
}
