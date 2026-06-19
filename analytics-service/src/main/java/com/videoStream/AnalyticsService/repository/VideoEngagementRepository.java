package com.videoStream.AnalyticsService.repository;

import com.videoStream.AnalyticsService.model.VideoEngagement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VideoEngagementRepository extends JpaRepository<VideoEngagement, Long> {
    Optional<VideoEngagement> findByVideoId(Long videoId);
}
