package com.videoStream.VideoService.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum VideoStatus {
    PROCESSING("Processing"),
    READY("Ready");

    private final String value;

    VideoStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public static VideoStatus fromValue(String value) {
        for (VideoStatus status : VideoStatus.values()) {
            if (status.getValue().equalsIgnoreCase(value)) {
                return status;
            }
        }
        return PROCESSING; // Default fallback
    }
}
