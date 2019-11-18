package ru.vasyunin.springcloudrive.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@NoArgsConstructor
@Data
@Table(name = "directories")
public class DirectoryItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "dirname")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent", nullable = true)
    private DirectoryItem parent;

    @Column(name = "parent", insertable = false, updatable = false)
    private Long parentId;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
    private List<DirectoryItem> subdirs;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "directory")
    private List<FileEntity> files;

    @Override
    public String toString() {
        return "DirectoryItem{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", parentId=" + parentId +
                '}';
    }

    public DirectoryItem(DirectoryItem parent, String name, User user) {
        this.name = name;
        this.user = user;
        this.parent = parent;
    }
}
