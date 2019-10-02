package ru.vasyunin.springcloudrive.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;
import ru.vasyunin.springcloudrive.dto.FileItemTDO;
import ru.vasyunin.springcloudrive.entity.DirectoryItem;
import ru.vasyunin.springcloudrive.entity.FileItem;
import ru.vasyunin.springcloudrive.entity.User;
import ru.vasyunin.springcloudrive.repository.DirectoryRepository;
import ru.vasyunin.springcloudrive.repository.FilesRepository;
import ru.vasyunin.springcloudrive.repository.UserRepository;
import ru.vasyunin.springcloudrive.utils.FileChunkInfo;
import ru.vasyunin.springcloudrive.utils.FileChunks;
import ru.vasyunin.springcloudrive.utils.FileUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
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
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileItem.getOriginFilename())
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

    /**
     * Function creates record in database about file (isComplited = false)
     * @param user
     * @param chunkInfo
     * @return FileItem object of file
     */
    public FileItem processChunkInDb(User user, FileChunkInfo chunkInfo){
        DirectoryItem dir = directoryRepository.findDirectoryItemByUserAndId(user, chunkInfo.relativePath);
        if (dir == null)
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);

        FileItem item = filesRepository.findFileItemByUserAndAndDirectoryAndFilename(user, dir, chunkInfo.localFilename);
        if (item != null) {
            if (!item.isCompleted()) return item;
            throw new HttpClientErrorException(HttpStatus.NOT_ACCEPTABLE);
        }

        item = new FileItem();
        item.setCompleted(false);
        item.setOriginFilename(chunkInfo.filename);
        item.setFilename(chunkInfo.localFilename);
        item.setSize(chunkInfo.totalSize);
        item.setUser(user);
        item.setLast_modified(LocalDateTime.now());
        item.setDirectory(dir);
        item.setType(FileUtils.getFileExtension(chunkInfo.filename));
        return filesRepository.save(item);
    }

    /**
     * Function adds chunk to target file
     * @param user
     * @param chunkInfo
     * @param file
     */
    public void processChunk(User user, FileChunkInfo chunkInfo, MultipartFile file){
        String filename = STORAGE + File.separator + user.getId() + File.separator + chunkInfo.localFilename;
        try (RandomAccessFile raf = new RandomAccessFile(filename, "rw")){
            raf.seek(chunkInfo.offset);
            raf.write(file.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            throw new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void setFileComplited(FileItem fileItem){
        fileItem.setCompleted(true);
        filesRepository.saveAndFlush(fileItem);
    }

}
