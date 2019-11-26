package ru.vasyunin.springcloudrive.service;

import com.ibm.icu.text.Transliterator;
import net.sf.jasperreports.engine.util.FileBufferedOutputStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
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

import javax.imageio.stream.FileImageOutputStream;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class FilesService {

    @Value("${cloudrive.storage.directory}")
    private String STORAGE_DIR;

    @Value("${cloudrive.storage.previewfolder}")
    private String PREVIEW_DIR;

    private final FilesRepository filesRepository;
    private final FilePreviewRepository previewRepository;
    private final DirectoryRepository directoryRepository;
    private final EntityManager entityManager;

    public FilesService(FilesRepository filesRepository, FilePreviewRepository previewRepository, DirectoryRepository directoryRepository, EntityManager entityManager) {
        this.filesRepository = filesRepository;
        this.previewRepository = previewRepository;
        this.directoryRepository = directoryRepository;
        this.entityManager = entityManager;
    }

    /**
     * Get FileEntity by id
     *
     * @param user
     * @param id
     * @return
     */
    public FileEntity getFileById(User user, long id) {
        return filesRepository.findFileItemByUserAndId(user, id);
    }

    /**
     * Downloading file
     *
     * @param fileEntity
     * @param user
     * @return
     * @throws FileNotFoundException
     */
    public ResponseEntity<InputStreamResource> getFileResponse(FileEntity fileEntity, User user) throws FileNotFoundException {
        File file = new File(STORAGE_DIR + File.separator + user.getId() + File.separator + fileEntity.getFilename());
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

        Transliterator toLatinTrans = Transliterator.getInstance("Russian-Latin/BGN");

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\"" + toLatinTrans.transliterate(fileEntity.getOriginFilename()).replaceAll("[^A-Za-z0-9\\.]", "_") + "\"")
                .contentLength(file.length())
                .body(resource);
    }


    /**
     * Function return list of directories and files in directory
     *
     * @param currentDirectory
     * @return
     */
    public List<FileItemDto> getFilelistByDirectory(DirectoryItem currentDirectory) {
        List<FileItemDto> result = new ArrayList<>();

        // If parent directory exists show it as ".."
        if (currentDirectory.getParent() != null) {
            result.add(new FileItemDto(currentDirectory.getParentId(), "..", 0, "", null, true, 0));
        }

        // Add subdirectories to response
        result.addAll(currentDirectory.getSubdirs().stream()
                .sorted(Comparator.comparing(DirectoryItem::getName))
                .map(dir -> new FileItemDto(dir.getId(), dir.getName(), 0L, null, null, true, 0))
                .collect(Collectors.toList()));

        // Add filelist to response
        result.addAll(currentDirectory.getFiles().stream()
                .filter(FileEntity::isCompleted)
                .sorted(Comparator.comparing(FileEntity::getOriginFilename))
                .map(file -> new FileItemDto(file.getId(), file.getOriginFilename(), file.getSize(), file.getType(), file.getLast_modified(), false, file.getPreviews().size()))
                .collect(Collectors.toList()));

        return result;
    }

    /**
     * Function creates record in database about file (isComplited = false)
     *
     * @param user
     * @param chunkInfo
     * @return FileEntity object of file
     */
    public FileEntity processChunk(User user, FileChunkInfo chunkInfo, MultipartFile file) {
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

        String filename = STORAGE_DIR + File.separator + user.getId() + File.separator + chunkInfo.localFilename;
        try (RandomAccessFile raf = new RandomAccessFile(filename, "rw")) {
            raf.seek(chunkInfo.offset);
            raf.write(file.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            throw new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return item;
    }

    public void setFileComplited(FileEntity fileEntity) {
        fileEntity.setCompleted(true);
        filesRepository.saveAndFlush(fileEntity);
    }

    public boolean deleteFile(User user, long fileId) {
        FileEntity fileEntity = filesRepository.findFileItemByUserAndId(user, fileId);
        if (fileEntity == null) return false;

        System.out.println(fileEntity.getPreviews());
        if (fileEntity.getPreviews().size() > 0) {
            for (FilePreview fpv : fileEntity.getPreviews()) {
                try {
                    Files.delete(Paths.get(FileUtils.getPath(STORAGE_DIR, user.getId().toString(), PREVIEW_DIR, fpv.getFilename())));
                    previewRepository.deleteById(fpv.getId());
                } catch (NoSuchFileException e) {
                    previewRepository.deleteById(fpv.getId());
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }

        try {
            Files.delete(Paths.get(STORAGE_DIR + File.separator + user.getId() + File.separator + fileEntity.getFilename()));
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

    public boolean createPreviewFile(User user, FileEntity fileEntity) {
        previewRepository.deleteFilePreviewsByFile(fileEntity);
        File file = new File(FileUtils.getPath(STORAGE_DIR, user.getId().toString(), fileEntity.getFilename()));
        if (fileEntity.getFileType() == FileType.IMAGE) {
            try {
                FilePreview fpv = new FilePreview(fileEntity);
                previewRepository.save(fpv);
                entityManager.refresh(fpv);
                FImage image = ImageUtilities.readF(file);
                image = ResizeProcessor.resizeMax(image, 200);
                ImageUtilities.write(image, "gif", new File(FileUtils.getPath(STORAGE_DIR, user.getId().toString(), PREVIEW_DIR, fpv.getFilename())));
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } else if (fileEntity.getFileType() == FileType.PDF) {
            try {
                PDDocument document = PDDocument.load(file);
                PDFRenderer pdfRenderer = new PDFRenderer(document);
                System.out.println(document.getNumberOfPages());
                for (int i = 0; i < document.getNumberOfPages() && i < 5; i++) {
                    FilePreview fpv = new FilePreview(fileEntity);
                    previewRepository.save(fpv);
                    entityManager.refresh(fpv);

                    BufferedImage bim = pdfRenderer.renderImageWithDPI(i, 50, ImageType.RGB);
                    FileOutputStream fos = new FileOutputStream(new File(FileUtils.getPath(STORAGE_DIR, user.getId().toString(), PREVIEW_DIR, fpv.getFilename())));
                    ImageIOUtil.writeImage(bim, "gif", fos, 50, 1f);
                    fos.close();
                }
                document.close();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }


    /**
     * Downloading file
     *
     * @param fileEntity
     * @param user
     * @return
     * @throws FileNotFoundException
     */
    public ResponseEntity<InputStreamResource> getFilePreviewResponse(FileEntity fileEntity, User user) throws FileNotFoundException {
//        File file = new File(STORAGE_DIR + File.separator + user.getId() + File.separator + PREVIEW_DIR + File.separator  + fileEntity.getPreviews().get(0));
        File file = new File(FileUtils.getPath(STORAGE_DIR, user.getId().toString(), PREVIEW_DIR, fileEntity.getPreviews().get(0).getFilename()));
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\"image" + fileEntity.getFilename() + ".gif\"")
                .contentLength(file.length())
                .body(resource);
    }

}
