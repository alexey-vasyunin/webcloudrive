package ru.vasyunin.springcloudrive.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.vasyunin.springcloudrive.entity.DirectoryItem;
import ru.vasyunin.springcloudrive.entity.User;

import java.util.List;

@Repository
public interface DirectoryRepository extends JpaRepository<DirectoryItem, Long> {
    DirectoryItem findDirectoryItemByUserAndParentIsNull(User user);
    DirectoryItem findDirectoryItemByUserAndParentId(User user, Long parentId);
    DirectoryItem findDirectoryItemByUserAndId(User user, long id);

    @Modifying
    @Query("update DirectoryItem di set di.name=:name where di.user=:user and di.id=:id")
    int setName(User user, long id, String name);
}
