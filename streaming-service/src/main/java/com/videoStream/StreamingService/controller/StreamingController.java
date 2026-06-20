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

    @GetMapping("/{fileName:.+}")
    public ResponseEntity<?> streamVideo(
            @PathVariable String fileName,
            @RequestHeader HttpHeaders headers) throws IOException {

        Resource videoResource = streamingService.getVideoResource(fileName);
        MediaType mediaType = MediaTypeFactory.getMediaType(videoResource)
                .orElse(MediaType.parseMediaType("video/mp4"));

        if (headers.getRange().isEmpty()) {
            return ResponseEntity.ok()
                    .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                    .contentType(mediaType)
                    .body(videoResource);
        }

        ResourceRegion region = streamingService.getVideoRegion(fileName, headers);

        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                .contentType(mediaType)
                .body(region);
    }
}
