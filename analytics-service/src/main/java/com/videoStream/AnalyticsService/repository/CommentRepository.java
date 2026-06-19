package com.videoStream.AnalyticsService.repository;

import com.videoStream.AnalyticsService.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByVideoIdOrderByCreatedAtDesc(Long videoId);
}
