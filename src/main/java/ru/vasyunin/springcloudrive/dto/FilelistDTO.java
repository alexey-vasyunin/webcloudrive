package ru.vasyunin.springcloudrive.dto;

import lombok.Data;

import java.util.List;

@Data
public class FilelistDTO {
    private List<FileItemTDO> content;
    private Long currentDirectory;

    public FilelistDTO(List<FileItemTDO> content, Long currentDirectory) {
        this.content = content;
        this.currentDirectory = currentDirectory;
    }
}
