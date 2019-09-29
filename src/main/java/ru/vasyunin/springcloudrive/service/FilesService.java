package ru.vasyunin.springcloudrive.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.vasyunin.springcloudrive.dto.FileItemTDO;
import ru.vasyunin.springcloudrive.entity.DirectoryItem;
import ru.vasyunin.springcloudrive.entity.FileItem;
import ru.vasyunin.springcloudrive.entity.User;
import ru.vasyunin.springcloudrive.repository.DirectoryRepository;
import ru.vasyunin.springcloudrive.repository.FilesRepository;
import ru.vasyunin.springcloudrive.repository.UserRepository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilesService {

    @Value("${cloudrive.storage.directory}")
    private String STORAGE;

    private final FilesRepository filesRepository;
    private final DirectoryRepository directoryRepository;
    private final UserRepository userRepository;

    public FilesService(FilesRepository filesRepository, DirectoryRepository directoryRepository, UserRepository userRepository) {
        this.filesRepository = filesRepository;
        this.directoryRepository = directoryRepository;
        this.userRepository = userRepository;
    }

    public List<FileItemTDO> getFilesInDirectory(User user, Long id) {
        return filesRepository.findAllByUser(user).stream().map(file -> {
            return new FileItemTDO(file.getId(), file.getOriginFilename(), file.getSize(), file.getType(), file.getLast_modified(), false);
        }).collect(Collectors.toList());
    }

    public FileItem getFileById(User user, long id){
        return filesRepository.findFileItemByUserAndId(user, id);
    }

    public ResponseEntity<InputStreamResource> getFile(FileItem fileItem, User user) throws FileNotFoundException {
        File file = new File(STORAGE + File.separator + user.getId() + File.separator  + fileItem.getFilename());
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileItem.getOriginFilename() + "." + fileItem.getType())
                .contentLength(file.length())
                .body(resource);
    }

    /**
     * Function return list of directories and files in directory
     * @param currentDirectory
     * @return
     */
    public List<FileItemTDO> getFilelistByDirectory(DirectoryItem currentDirectory){
        List<FileItemTDO> result = new ArrayList<>();

        // If parent directory exists show it as ".."
        if (currentDirectory.getParent() != null){
            result.add(new FileItemTDO(currentDirectory.getParentId(), "..", 0, "", null, true));
        }

        // Add subdirectories to response
        result.addAll(currentDirectory.getSubdirs().stream()
                .map(dir -> {
                    return new FileItemTDO(dir.getId(), dir.getName(), 0L, null, null, true);
                }).collect(Collectors.toList()));

        // Add filelist to response
        result.addAll(currentDirectory.getFiles().stream()
                .filter(FileItem::isCompleted)
                .map(file -> {
                    return new FileItemTDO(file.getId(), file.getOriginFilename(), file.getSize(), file.getType(), file.getLast_modified(), false);
                }).collect(Collectors.toList()));

        return result;
    }
}
