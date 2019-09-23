package ru.vasyunin.springcloudrive.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@Table(name = "files")
public class FileItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    private Long id;

    @Column(name = "filename")
    private String filename;

    @Column(name = "origin_filename")
    private String originFilename;

    @Column(name = "filesize")
    private long size;

    @Column(name = "filetype")
    private String type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "directory_id")
    private DirectoryItem directory;

    @Column(name = "last_modified")
    private LocalDateTime last_modified;

    @Override
    public String toString() {
        return "FileItem{" +
                "id=" + id +
                ", filename='" + filename + '\'' +
                ", size=" + size +
                ", type='" + type + '\'' +
                ", last_modified=" + last_modified +
                '}';
    }
}
