package com.videoStream.VideoService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoRequest {
    private String title;
    private String description;
    private String videoUrl;
    private String thumbnailUrl;
    private String userId;
    private String status;
}
