package com.videoStream.VideoService.controller;

import com.videoStream.VideoService.dto.VideoApiResponse;
import com.videoStream.VideoService.dto.VideoRequest;
import com.videoStream.VideoService.model.Video;
import com.videoStream.VideoService.service.VideoService;
import static com.videoStream.VideoService.util.MessageConstants.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/videos")
@Tag(name = "Video Controller", description = "Endpoints for managing video metadata and catalog")
public class VideoController {

    private final VideoService videoService;

    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    @GetMapping
    @Operation(summary = "Get all videos", description = "Fetches a list of all active videos that have status 'Ready'")
    public ResponseEntity<VideoApiResponse<List<Video>>> getAllVideos() {
        List<Video> videos = videoService.getAllVideos();
        return ResponseEntity.ok(VideoApiResponse.success(VIDEOS_FETCHED, videos));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get video by ID", description = "Retrieves metadata of a single active video by its unique ID")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Video details successfully retrieved"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Video not found")
    })
    public ResponseEntity<VideoApiResponse<Video>> getVideoById(@PathVariable Long id) {
        Video video = videoService.getVideoById(id);
        return ResponseEntity.ok(VideoApiResponse.success(VIDEO_FETCHED, video));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get user videos", description = "Fetches all active videos uploaded by a specific user")
    public ResponseEntity<VideoApiResponse<List<Video>>> getVideosByUserId(@PathVariable String userId) {
        List<Video> videos = videoService.getVideosByUserId(userId);
        return ResponseEntity.ok(VideoApiResponse.success(USER_VIDEOS_FETCHED, videos));
    }

    @GetMapping("/search")
    @Operation(summary = "Search videos", description = "Searches 'Ready' status videos matching the query term in titles or descriptions")
    public ResponseEntity<VideoApiResponse<List<Video>>> searchVideos(@RequestParam("query") String query) {
        List<Video> videos = videoService.searchVideos(query);
        return ResponseEntity.ok(VideoApiResponse.success(SEARCH_RESULTS_FETCHED, videos));
    }

    @PostMapping
    @Operation(summary = "Create video metadata", description = "Saves new video metadata records initially with 'Processing' status")
    public ResponseEntity<VideoApiResponse<Video>> createVideo(@RequestBody VideoRequest request) {
        Video savedVideo = videoService.createVideo(request);
        return ResponseEntity.ok(VideoApiResponse.success(VIDEO_CREATED, savedVideo));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update video metadata", description = "Updates fields of an existing video record by ID")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Video metadata successfully updated"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Video not found")
    })
    public ResponseEntity<VideoApiResponse<Video>> updateVideo(@PathVariable Long id, @RequestBody VideoRequest request) {
        Video updatedVideo = videoService.updateVideo(id, request);
        return ResponseEntity.ok(VideoApiResponse.success(VIDEO_UPDATED, updatedVideo));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete video", description = "Marks a video record as deleted by ID")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Video successfully marked deleted"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Video not found")
    })
    public ResponseEntity<VideoApiResponse<Void>> deleteVideo(@PathVariable Long id) {
        videoService.deleteVideo(id);
        return ResponseEntity.ok(VideoApiResponse.success(VIDEO_DELETED, null));
    }

    @PostMapping("/{id}/view")
    @Operation(summary = "Increment view count", description = "Increments video view counter by 1")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "View count successfully incremented"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Video not found")
    })
    public ResponseEntity<VideoApiResponse<Video>> incrementViews(@PathVariable Long id) {
        Video updatedVideo = videoService.incrementViews(id);
        return ResponseEntity.ok(VideoApiResponse.success(VIEW_INCREMENTED, updatedVideo));
    }
}
