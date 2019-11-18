package ru.vasyunin.springcloudrive.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Table(name = "files_previews")
@NoArgsConstructor
public class FilePreview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "preview_id")
    private Long id;

    @Column(name = "filename", insertable = false, updatable = false, columnDefinition = "uuid DEFAULT uuid_generate_v4()")
    private String filename;

    @ManyToOne
    @JoinColumn(name = "file_id")
    private FileEntity file;

    public FilePreview(FileEntity file) {
        this.file = file;
    }
}
