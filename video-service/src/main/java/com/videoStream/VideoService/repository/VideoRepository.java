package com.videoStream.VideoService.repository;

import com.videoStream.VideoService.model.Video;
import com.videoStream.VideoService.model.VideoStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {

    List<Video> findByIsActiveTrueAndIsDeletedFalseAndStatus(VideoStatus status);

    List<Video> findByUserIdAndIsDeletedFalse(String userId);

    @Query("SELECT v FROM Video v WHERE v.isActive = true AND v.isDeleted = false AND v.status = :status AND " +
           "(LOWER(v.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(v.description) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Video> searchVideos(@Param("status") VideoStatus status, @Param("query") String query);
}
