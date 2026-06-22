package com.videoStream.VideoService.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "videos")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Video extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "video_url", nullable = false)
    private String videoUrl;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Builder.Default
    private VideoStatus status = VideoStatus.PROCESSING;

    @Builder.Default
    private int views = 0;

    @Column(name = "visibility")
    @Builder.Default
    private String visibility = "PUBLIC";

    @Column(name = "auto_subtitles")
    @Builder.Default
    private Boolean autoSubtitles = false;

    @Column(name = "interactive_cards")
    @Builder.Default
    private Boolean interactiveCards = false;

    @Column(name = "copyright_passed")
    @Builder.Default
    private Boolean copyrightPassed = true;
}
