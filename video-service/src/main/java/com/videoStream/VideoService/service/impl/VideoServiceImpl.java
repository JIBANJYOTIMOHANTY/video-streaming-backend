package com.videoStream.VideoService.service.impl;

import com.videoStream.VideoService.dto.VideoRequest;
import com.videoStream.VideoService.exception.VideoNotFoundException;
import com.videoStream.VideoService.model.Video;
import com.videoStream.VideoService.model.VideoStatus;
import com.videoStream.VideoService.repository.VideoRepository;
import com.videoStream.VideoService.service.VideoService;
import static com.videoStream.VideoService.util.MessageConstants.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VideoServiceImpl implements VideoService {

    private final VideoRepository videoRepository;

    public VideoServiceImpl(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
    }

    @Override
    public List<Video> getAllVideos() {
        return videoRepository.findActivePublicVideos(VideoStatus.READY);
    }

    @Override
    public Video getVideoById(Long id) {
        return videoRepository.findById(id)
                .filter(v -> !v.isDeleted())
                .orElseThrow(() -> new VideoNotFoundException(VIDEO_NOT_FOUND + id));
    }

    @Override
    public List<Video> getVideosByUserId(String userId) {
        return videoRepository.findByUserIdAndIsDeletedFalse(userId);
    }

    @Override
    public List<Video> searchVideos(String query) {
        return videoRepository.searchVideos(VideoStatus.READY, query);
    }

    @Override
    public Video createVideo(VideoRequest request) {
        Video video = Video.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .videoUrl(request.getVideoUrl())
                .thumbnailUrl(request.getThumbnailUrl())
                .userId(request.getUserId())
                .status(request.getStatus() != null ? VideoStatus.fromValue(request.getStatus()) : VideoStatus.PROCESSING)
                .views(0)
                .visibility(request.getVisibility() != null ? request.getVisibility().toUpperCase() : "PUBLIC")
                .autoSubtitles(request.getAutoSubtitles() != null ? request.getAutoSubtitles() : false)
                .interactiveCards(request.getInteractiveCards() != null ? request.getInteractiveCards() : false)
                .copyrightPassed(request.getCopyrightPassed() != null ? request.getCopyrightPassed() : true)
                .build();
        return videoRepository.save(video);
    }

    @Override
    public Video updateVideo(Long id, VideoRequest request) {
        Video video = videoRepository.findById(id)
                .filter(v -> !v.isDeleted())
                .orElseThrow(() -> new VideoNotFoundException(VIDEO_NOT_FOUND + id));

        if (request.getTitle() != null) video.setTitle(request.getTitle());
        if (request.getDescription() != null) video.setDescription(request.getDescription());
        if (request.getThumbnailUrl() != null) video.setThumbnailUrl(request.getThumbnailUrl());
        if (request.getVideoUrl() != null) video.setVideoUrl(request.getVideoUrl());
        if (request.getStatus() != null) video.setStatus(VideoStatus.fromValue(request.getStatus()));
        if (request.getVisibility() != null) video.setVisibility(request.getVisibility().toUpperCase());
        if (request.getAutoSubtitles() != null) video.setAutoSubtitles(request.getAutoSubtitles());
        if (request.getInteractiveCards() != null) video.setInteractiveCards(request.getInteractiveCards());
        if (request.getCopyrightPassed() != null) video.setCopyrightPassed(request.getCopyrightPassed());

        return videoRepository.save(video);
    }

    @Override
    public void deleteVideo(Long id) {
        Video video = videoRepository.findById(id)
                .filter(v -> !v.isDeleted())
                .orElseThrow(() -> new VideoNotFoundException(VIDEO_NOT_FOUND + id));

        video.setDeleted(true);
        videoRepository.save(video);
    }

    @Override
    public Video incrementViews(Long id) {
        Video video = videoRepository.findById(id)
                .filter(v -> !v.isDeleted())
                .orElseThrow(() -> new VideoNotFoundException(VIDEO_NOT_FOUND + id));

        video.setViews(video.getViews() + 1);
        return videoRepository.save(video);
    }
}
