package ru.vasyunin.springcloudrive.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class FileItem {
    private Long id;
    private String filename;
    private long size;
    private String type;
    private LocalDateTime dateModified;

    public FileItem(Long id, String filename, long size, String type, LocalDateTime dateModified) {
        this.id = id;
        this.filename = filename;
        this.size = size;
        this.type = type;
        this.dateModified = dateModified;
    }

    @Override
    public String toString() {
        return "FileItem[id: " + id + ", filename: " + filename + ", size: " + size + ", type: " + type + "]";
    }
}
