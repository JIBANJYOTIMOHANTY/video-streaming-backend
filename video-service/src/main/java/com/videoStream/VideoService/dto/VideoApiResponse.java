package com.videoStream.VideoService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VideoApiResponse<T> {
    private int status;
    private String message;
    private T data;

    public static <T> VideoApiResponse<T> success(String message, T data) {
        return VideoApiResponse.<T>builder()
                .status(0)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> VideoApiResponse<T> error(String message) {
        return VideoApiResponse.<T>builder()
                .status(1)
                .message(message)
                .data(null)
                .build();
    }
}
