package ru.vasyunin.springcloudrive.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.vasyunin.springcloudrive.entity.DirectoryItem;
import ru.vasyunin.springcloudrive.entity.User;
import ru.vasyunin.springcloudrive.repository.DirectoryRepository;
import ru.vasyunin.springcloudrive.repository.FilesRepository;
import ru.vasyunin.springcloudrive.repository.UserRepository;

import java.util.List;

@Service
public class DirectoryService {
    private final DirectoryRepository directoryRepository;
    private final UserRepository userRepository;
    private final FilesRepository filesRepository;


    @Autowired
    public DirectoryService(DirectoryRepository directoryRepository, UserRepository userRepository, FilesRepository filesRepository) {
        this.directoryRepository = directoryRepository;
        this.userRepository = userRepository;
        this.filesRepository = filesRepository;
    }

    public DirectoryItem getDirectoryByParent(User user, Long id){
        return directoryRepository.findDirectoryItemByUserAndParentId(user, id);
    }

    public DirectoryItem getDirectoryById(User user, long id){
        return directoryRepository.findDirectoryItemByUserAndId(user, id);
    }

    public DirectoryItem getRootDirectory(User user){
        return directoryRepository.findDirectoryItemByUserAndParentIsNull(user);
    }

    public DirectoryItem createRootDirectory(User user){
        DirectoryItem item = new DirectoryItem();
        item.setUser(user);
        item.setName("root");
        return directoryRepository.save(item);
    }

}
