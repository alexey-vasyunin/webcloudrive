package ru.vasyunin.springcloudrive.service;

import com.ibm.icu.text.Transliterator;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.processing.resize.ResizeProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;
import ru.vasyunin.springcloudrive.dto.FileItemDto;
import ru.vasyunin.springcloudrive.dto.FilePreviewDto;
import ru.vasyunin.springcloudrive.entity.DirectoryItem;
import ru.vasyunin.springcloudrive.entity.FileEntity;
import ru.vasyunin.springcloudrive.entity.FilePreview;
import ru.vasyunin.springcloudrive.entity.User;
import ru.vasyunin.springcloudrive.repository.DirectoryRepository;
import ru.vasyunin.springcloudrive.repository.FilePreviewRepository;
import ru.vasyunin.springcloudrive.repository.FilesRepository;
import ru.vasyunin.springcloudrive.utils.FileChunkInfo;
import ru.vasyunin.springcloudrive.utils.FileType;
import ru.vasyunin.springcloudrive.utils.FileUtils;

import javax.transaction.Transactional;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class FilesService {

    @Value("${cloudrive.storage.directory}")
    private String STORAGE;

    private final FilesRepository filesRepository;
    private final FilePreviewRepository previewRepository;
    private final DirectoryRepository directoryRepository;

    public FilesService(FilesRepository filesRepository, FilePreviewRepository previewRepository, DirectoryRepository directoryRepository) {
        this.filesRepository = filesRepository;
        this.previewRepository = previewRepository;
        this.directoryRepository = directoryRepository;
    }

    /**
     * Get FileEntity by id
     * @param user
     * @param id
     * @return
     */
    public FileEntity getFileById(User user, long id){
        return filesRepository.findFileItemByUserAndId(user, id);
    }

    /**
     * Downloading file
     * @param fileEntity
     * @param user
     * @return
     * @throws FileNotFoundException
     */
    public ResponseEntity<InputStreamResource> getFileResponse(FileEntity fileEntity, User user) throws FileNotFoundException {
        File file = new File(STORAGE + File.separator + user.getId() + File.separator  + fileEntity.getFilename());
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

        Transliterator toLatinTrans = Transliterator.getInstance("Russian-Latin/BGN");

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\"" + toLatinTrans.transliterate(fileEntity.getOriginFilename()).replaceAll("[^A-Za-z0-9\\.]", "_") + "\"")
                .contentLength(file.length())
                .body(resource);
    }


        /**
         * Function return list of directories and files in directory
         * @param currentDirectory
         * @return
         */
    public List<FileItemDto> getFilelistByDirectory(DirectoryItem currentDirectory){
        List<FileItemDto> result = new ArrayList<>();

        // If parent directory exists show it as ".."
        if (currentDirectory.getParent() != null){
            result.add(new FileItemDto(currentDirectory.getParentId(), "..", 0, "", null, true, null));
        }

        // Add subdirectories to response
        result.addAll(currentDirectory.getSubdirs().stream()
                .sorted(Comparator.comparing(DirectoryItem::getName))
                .map(dir -> new FileItemDto(dir.getId(), dir.getName(), 0L, null, null, true, null))
                .collect(Collectors.toList()));

        // Add filelist to response
        result.addAll(currentDirectory.getFiles().stream()
                .filter(FileEntity::isCompleted)
                .sorted(Comparator.comparing(FileEntity::getOriginFilename))
                .map(file -> new FileItemDto(file.getId(), file.getOriginFilename(), file.getSize(), file.getType(), file.getLast_modified(), false, new FilePreviewDto(file)))
                .collect(Collectors.toList()));

        return result;
    }

    /**
     * Function creates record in database about file (isComplited = false)
     * @param user
     * @param chunkInfo
     * @return FileEntity object of file
     */
    public FileEntity processChunk(User user, FileChunkInfo chunkInfo, MultipartFile file){
        DirectoryItem dir = directoryRepository.findDirectoryItemByUserAndId(user, chunkInfo.relativePath);
        if (dir == null)
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);

        FileEntity item = filesRepository.findFileItemByUserAndDirectoryAndFilename(user, dir, chunkInfo.localFilename);
        if (item == null) {
            item = new FileEntity();
            item.setCompleted(false);
            item.setOriginFilename(chunkInfo.filename);
            item.setFilename(chunkInfo.localFilename);
            item.setSize(chunkInfo.totalSize);
            item.setUser(user);
            item.setLast_modified(LocalDateTime.now());
            item.setDirectory(dir);
            item.setType(FileUtils.getFileExtension(chunkInfo.filename));
            item = filesRepository.save(item);
        } else {
            if (item.isCompleted()) {
                throw new HttpClientErrorException(HttpStatus.NOT_ACCEPTABLE);
            }
        }

        String filename = STORAGE + File.separator + user.getId() + File.separator + chunkInfo.localFilename;
        try (RandomAccessFile raf = new RandomAccessFile(filename, "rw")) {
            raf.seek(chunkInfo.offset);
            raf.write(file.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            throw new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return item;
    }

    public void setFileComplited(FileEntity fileEntity){
        fileEntity.setCompleted(true);
        filesRepository.saveAndFlush(fileEntity);
    }

    public boolean deleteFile(User user, long fileId){
        FileEntity fileEntity = filesRepository.findFileItemByUserAndId(user, fileId);
        if (fileEntity == null) return false;

        try {
            Files.delete(Paths.get(STORAGE + File.separator + user.getId() + File.separator  + fileEntity.getFilename()));
            filesRepository.deleteById(fileId);
            return true;
        } catch (NoSuchFileException e) {
            filesRepository.deleteById(fileId);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateFile(User user, long id, String name) {
        return filesRepository.setName(user, id, name) > 0;
    }

    public boolean createPreviewFile(User user, FileEntity fileEntity){
        previewRepository.deleteFilePreviewsByFile(fileEntity);
        if (fileEntity.getFileType() == FileType.IMAGE){
            try {
                FImage image = ImageUtilities.readF(new File(STORAGE + File.separator + user.getId() + File.separator  + fileEntity.getFilename()));
                image = ResizeProcessor.resizeMax(image, 200);
                ImageUtilities.write(image, "gif", new File(STORAGE + File.separator + user.getId() + File.separator  + "previews" + File.separator + fileEntity.getFilename()));
                previewRepository.save(new FilePreview(fileEntity));
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }
}
