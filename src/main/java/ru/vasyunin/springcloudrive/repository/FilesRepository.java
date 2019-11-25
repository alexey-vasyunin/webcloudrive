package ru.vasyunin.springcloudrive.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.vasyunin.springcloudrive.entity.DirectoryItem;
import ru.vasyunin.springcloudrive.entity.FileEntity;
import ru.vasyunin.springcloudrive.entity.User;

import java.util.List;

@Repository
public interface FilesRepository extends JpaRepository<FileEntity, Long> {
    List<FileEntity> findAllByUser(User user);
    FileEntity findFileItemByUserAndId(User user, long id);
    FileEntity findFileItemByUserAndDirectoryAndFilename(User user, DirectoryItem directory, String filename);
    boolean deleteFileItemByUserAndId(User user, long id);
    List<FileEntity> findFileItemsByUserAndDirectoryId(User user, long id);

    @Modifying
    @Query("update FileEntity fi set fi.originFilename=:name where fi.user=:user and fi.id=:id")
    int setName(User user, long id, String name);
}
