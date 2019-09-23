package ru.vasyunin.springcloudrive.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.vasyunin.springcloudrive.entity.DirectoryItem;
import ru.vasyunin.springcloudrive.entity.User;

import java.util.List;

@Repository
public interface DirectoryRepository extends JpaRepository<DirectoryItem, Long> {
    DirectoryItem findDirectoryItemByUserAndParentId(User user, Long parentId);
    DirectoryItem findDirectoryItemByUserAndId(User user, long id);
}
