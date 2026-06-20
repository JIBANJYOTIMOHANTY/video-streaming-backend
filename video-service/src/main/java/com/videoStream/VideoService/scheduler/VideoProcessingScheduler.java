package com.videoStream.VideoService.scheduler;

import com.videoStream.VideoService.model.Video;
import com.videoStream.VideoService.model.VideoStatus;
import com.videoStream.VideoService.repository.VideoRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class VideoProcessingScheduler {

    private final VideoRepository videoRepository;

    public VideoProcessingScheduler(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
    }

    @Scheduled(fixedDelay = 5000)
    public void processVideos() {
        List<Video> processingVideos = videoRepository.findByIsActiveTrueAndIsDeletedFalseAndStatus(VideoStatus.PROCESSING);
        if (!processingVideos.isEmpty()) {
            for (Video video : processingVideos) {
                video.setStatus(VideoStatus.READY);
                videoRepository.save(video);
                System.out.println("Video processed and set to READY: " + video.getTitle());
            }
        }
    }
}
