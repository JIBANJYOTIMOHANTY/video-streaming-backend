package com.videoStream.VideoService.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class VideoStatusConverter implements AttributeConverter<VideoStatus, String> {

    @Override
    public String convertToDatabaseColumn(VideoStatus attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getValue();
    }

    @Override
    public VideoStatus convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return VideoStatus.fromValue(dbData);
    }
}
