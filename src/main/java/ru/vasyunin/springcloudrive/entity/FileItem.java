package ru.vasyunin.springcloudrive.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;

@Data
@NoArgsConstructor
public class FileItem {
    private Long id;
    private String filename;
    private long size;
    private String type;

    public FileItem(Long id, String filename, long size, String type) {
        this.id = id;
        this.filename = filename;
        this.size = size;
        this.type = type;
    }

    @Override
    public String toString() {
        return "FileItem[id: " + id + ", filename: " + filename + ", size: " + size + ", type: " + type + "]";
    }
}
