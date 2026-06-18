package com.videoStream.VideoService.service;

import com.videoStream.VideoService.dto.VideoRequest;
import com.videoStream.VideoService.model.Video;
import java.util.List;

public interface VideoService {
    List<Video> getAllVideos();
    Video getVideoById(Long id);
    List<Video> getVideosByUserId(String userId);
    List<Video> searchVideos(String query);
    Video createVideo(VideoRequest request);
    Video updateVideo(Long id, VideoRequest request);
    void deleteVideo(Long id);
    Video incrementViews(Long id);
}
