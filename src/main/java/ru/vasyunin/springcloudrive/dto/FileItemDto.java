package ru.vasyunin.springcloudrive.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class FileItemDto {
    private final Long id;
    private final String filename;
    private final Long size;
    private final String type;
    private final LocalDateTime dateModified;
    private final boolean isDirectory;
    private final int countPreviews;

    public FileItemDto(Long id, String filename, long size, String type, LocalDateTime dateModified, boolean isDirectory, int countPreviews) {
        this.id = id;
        this.filename = filename;
        this.size = size;
        this.type = type;
        this.dateModified = dateModified;
        this.isDirectory = isDirectory;
        this.countPreviews = countPreviews;
    }

    @Override
    public String toString() {
        return "FileEntity[id: " + id + ", filename: " + filename + ", size: " + size + ", type: " + type + "]";
    }
}
