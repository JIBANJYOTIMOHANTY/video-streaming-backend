package com.videoStream.AnalyticsService.service;

import com.videoStream.AnalyticsService.model.Comment;
import com.videoStream.AnalyticsService.model.VideoEngagement;

import java.util.List;

public interface AnalyticsService {
    VideoEngagement trackView(Long videoId);
    VideoEngagement likeVideo(Long videoId);
    VideoEngagement dislikeVideo(Long videoId);
    VideoEngagement getEngagement(Long videoId);
    Comment addComment(Long videoId, String userId, String username, String content);
    List<Comment> getCommentsByVideo(Long videoId);
}
