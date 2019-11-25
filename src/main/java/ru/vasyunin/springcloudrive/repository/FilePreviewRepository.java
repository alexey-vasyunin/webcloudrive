package ru.vasyunin.springcloudrive.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vasyunin.springcloudrive.entity.FileEntity;
import ru.vasyunin.springcloudrive.entity.FilePreview;

public interface FilePreviewRepository extends JpaRepository<FilePreview, Long> {
    int deleteFilePreviewsByFile(FileEntity file);
}
