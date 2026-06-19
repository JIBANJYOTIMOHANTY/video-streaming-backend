package com.videoStream.AnalyticsService.controller;

import com.videoStream.AnalyticsService.dto.ApiResponse;
import com.videoStream.AnalyticsService.dto.CommentRequest;
import com.videoStream.AnalyticsService.model.Comment;
import com.videoStream.AnalyticsService.model.VideoEngagement;
import com.videoStream.AnalyticsService.service.AnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @PostMapping("/view/{videoId}")
    public ResponseEntity<ApiResponse<VideoEngagement>> trackView(@PathVariable Long videoId) {
        VideoEngagement engagement = analyticsService.trackView(videoId);
        return ResponseEntity.ok(ApiResponse.success("View tracked successfully", engagement));
    }

    @PostMapping("/like/{videoId}")
    public ResponseEntity<ApiResponse<VideoEngagement>> likeVideo(@PathVariable Long videoId) {
        VideoEngagement engagement = analyticsService.likeVideo(videoId);
        return ResponseEntity.ok(ApiResponse.success("Video liked successfully", engagement));
    }

    @PostMapping("/dislike/{videoId}")
    public ResponseEntity<ApiResponse<VideoEngagement>> dislikeVideo(@PathVariable Long videoId) {
        VideoEngagement engagement = analyticsService.dislikeVideo(videoId);
        return ResponseEntity.ok(ApiResponse.success("Video disliked successfully", engagement));
    }

    @GetMapping("/{videoId}")
    public ResponseEntity<ApiResponse<VideoEngagement>> getEngagement(@PathVariable Long videoId) {
        VideoEngagement engagement = analyticsService.getEngagement(videoId);
        return ResponseEntity.ok(ApiResponse.success("Engagement retrieved successfully", engagement));
    }

    @PostMapping("/{videoId}/comments")
    public ResponseEntity<ApiResponse<Comment>> addComment(
            @PathVariable Long videoId,
            @RequestBody CommentRequest request) {
        Comment comment = analyticsService.addComment(
                videoId,
                request.getUserId(),
                request.getUsername(),
                request.getContent()
        );
        return ResponseEntity.ok(ApiResponse.success("Comment added successfully", comment));
    }

    @GetMapping("/{videoId}/comments")
    public ResponseEntity<ApiResponse<List<Comment>>> getComments(@PathVariable Long videoId) {
        List<Comment> comments = analyticsService.getCommentsByVideo(videoId);
        return ResponseEntity.ok(ApiResponse.success("Comments retrieved successfully", comments));
    }
}
