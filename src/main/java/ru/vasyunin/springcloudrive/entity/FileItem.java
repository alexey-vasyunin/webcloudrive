package ru.vasyunin.springcloudrive.entity;

import lombok.Data;

import javax.persistence.Entity;

@Data
public class FileItem {
    private Long id;
    private String filename;
}
