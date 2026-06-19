package com.videoStream.AnalyticsService.service.impl;

import com.videoStream.AnalyticsService.model.Comment;
import com.videoStream.AnalyticsService.model.VideoEngagement;
import com.videoStream.AnalyticsService.repository.CommentRepository;
import com.videoStream.AnalyticsService.repository.VideoEngagementRepository;
import com.videoStream.AnalyticsService.service.AnalyticsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AnalyticsServiceImpl implements AnalyticsService {

    private final VideoEngagementRepository videoEngagementRepository;
    private final CommentRepository commentRepository;

    public AnalyticsServiceImpl(VideoEngagementRepository videoEngagementRepository, CommentRepository commentRepository) {
        this.videoEngagementRepository = videoEngagementRepository;
        this.commentRepository = commentRepository;
    }

    private VideoEngagement getOrCreateEngagement(Long videoId) {
        return videoEngagementRepository.findByVideoId(videoId)
                .orElseGet(() -> videoEngagementRepository.save(
                        VideoEngagement.builder()
                                .videoId(videoId)
                                .views(0)
                                .likes(0)
                                .dislikes(0)
                                .build()
                ));
    }

    @Override
    @Transactional
    public VideoEngagement trackView(Long videoId) {
        VideoEngagement engagement = getOrCreateEngagement(videoId);
        engagement.setViews(engagement.getViews() + 1);
        return videoEngagementRepository.save(engagement);
    }

    @Override
    @Transactional
    public VideoEngagement likeVideo(Long videoId) {
        VideoEngagement engagement = getOrCreateEngagement(videoId);
        engagement.setLikes(engagement.getLikes() + 1);
        return videoEngagementRepository.save(engagement);
    }

    @Override
    @Transactional
    public VideoEngagement dislikeVideo(Long videoId) {
        VideoEngagement engagement = getOrCreateEngagement(videoId);
        engagement.setDislikes(engagement.getDislikes() + 1);
        return videoEngagementRepository.save(engagement);
    }

    @Override
    @Transactional(readOnly = true)
    public VideoEngagement getEngagement(Long videoId) {
        return getOrCreateEngagement(videoId);
    }

    @Override
    @Transactional
    public Comment addComment(Long videoId, String userId, String username, String content) {
        // Ensure engagement record exists for the video
        getOrCreateEngagement(videoId);

        Comment comment = Comment.builder()
                .videoId(videoId)
                .userId(userId)
                .username(username)
                .content(content)
                .build();
        return commentRepository.save(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comment> getCommentsByVideo(Long videoId) {
        return commentRepository.findByVideoIdOrderByCreatedAtDesc(videoId);
    }
}
