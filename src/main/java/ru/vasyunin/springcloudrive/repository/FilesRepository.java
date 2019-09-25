package ru.vasyunin.springcloudrive.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.vasyunin.springcloudrive.entity.DirectoryItem;
import ru.vasyunin.springcloudrive.entity.FileItem;
import ru.vasyunin.springcloudrive.entity.User;

import java.io.File;
import java.util.List;

@Repository
public interface FilesRepository extends JpaRepository<FileItem, Long> {
    List<FileItem> findAllByUser(User user);
    FileItem findFileItemByUserAndId(User user, long id);
}
