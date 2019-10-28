package ru.vasyunin.springcloudrive.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.vasyunin.springcloudrive.entity.DirectoryItem;
import ru.vasyunin.springcloudrive.entity.User;
import ru.vasyunin.springcloudrive.repository.DirectoryRepository;
import ru.vasyunin.springcloudrive.repository.FilesRepository;
import ru.vasyunin.springcloudrive.repository.UserRepository;
import ru.vasyunin.springcloudrive.utils.FileUtils;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@Transactional
public class DirectoryService {
    @Value("${cloudrive.storage.directory}")
    private String STORAGE;

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
        FileUtils.createSubfolder(STORAGE + File.separator + user.getId());
        return directoryRepository.save(item);
    }

    public void deleteDirectory(User user, long id) {

        DirectoryItem di = directoryRepository.findDirectoryItemByUserAndId(user, id);
        List<DirectoryItem> subdirs = di.getSubdirs();
        subdirs.forEach(directoryItem -> deleteDirectory(user, directoryItem.getId()));

        final AtomicBoolean ok = new AtomicBoolean(true);
        filesRepository.findFileItemsByUserAndDirectoryId(user, id)
                .forEach(fileItem -> {
                    try {
                        Files.delete(Paths.get(STORAGE + File.separator + user.getId() + File.separator + fileItem.getFilename()));
                        filesRepository.delete(fileItem);
                    } catch (NoSuchFileException e) {
                        filesRepository.delete(fileItem);
                    } catch (IOException e) {
                        ok.set(false);
                        e.printStackTrace();
                    }
                });
        if (ok.get())
            directoryRepository.deleteById(id);
    }

    public boolean addNewDirectory(User user, long parentid, String name){
        DirectoryItem parent = directoryRepository.findDirectoryItemByUserAndId(user, parentid);
        DirectoryItem newDirectory = new DirectoryItem(parent, name, user);
        directoryRepository.save(newDirectory);
        parent.getSubdirs().add(newDirectory);
        return true;
    }

}
