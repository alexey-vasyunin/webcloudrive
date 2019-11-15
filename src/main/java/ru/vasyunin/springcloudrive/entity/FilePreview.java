package ru.vasyunin.springcloudrive.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class FilePreview {
    @Id
    @Column(name = "preview_id")
    private Long id;

    @Column(name = "filename")
    private String filename;

    @ManyToOne
    @JoinColumn(name = "file_id")
    private FileItem file;
}
