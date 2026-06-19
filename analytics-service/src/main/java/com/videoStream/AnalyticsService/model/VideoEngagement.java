package com.videoStream.AnalyticsService.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "video_engagements")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VideoEngagement extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "video_id", nullable = false, unique = true)
    private Long videoId;

    @Builder.Default
    private int views = 0;

    @Builder.Default
    private int likes = 0;

    @Builder.Default
    private int dislikes = 0;
}
