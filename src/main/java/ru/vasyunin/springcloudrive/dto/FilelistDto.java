package ru.vasyunin.springcloudrive.dto;

import lombok.Data;

import java.util.List;

@Data
public class FilelistDto {
    private List<FileItemDto> content;
    private Long currentDirectory;

    public FilelistDto(List<FileItemDto> content, Long currentDirectory) {
        this.content = content;
        this.currentDirectory = currentDirectory;
    }
}
