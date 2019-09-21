package ru.vasyunin.springcloudrive.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;

@Data
@NoArgsConstructor
public class FileItem {
    private Long id;
    private String filename;

    public FileItem(Long id, String filename) {
        this.id = id;
        this.filename = filename;
    }
}
