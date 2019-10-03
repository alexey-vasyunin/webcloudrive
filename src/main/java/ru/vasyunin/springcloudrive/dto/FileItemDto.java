package ru.vasyunin.springcloudrive.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
public class FileItemDto {
    private final Long id;
    private final String filename;
    private final Long size;
    private final String type;
    private final LocalDateTime dateModified;
    private final boolean isDirectory;

    public FileItemDto(Long id, String filename, long size, String type, LocalDateTime dateModified, boolean isDirectory) {
        this.id = id;
        this.filename = filename;
        this.size = size;
        this.type = type;
        this.dateModified = dateModified;
        this.isDirectory = isDirectory;
    }

    @Override
    public String toString() {
        return "FileItem[id: " + id + ", filename: " + filename + ", size: " + size + ", type: " + type + "]";
    }
}
