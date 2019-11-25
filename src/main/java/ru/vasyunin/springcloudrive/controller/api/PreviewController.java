package ru.vasyunin.springcloudrive.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.vasyunin.springcloudrive.entity.FileEntity;
import ru.vasyunin.springcloudrive.entity.User;
import ru.vasyunin.springcloudrive.service.FilesService;
import ru.vasyunin.springcloudrive.utils.FileChunkInfo;
import ru.vasyunin.springcloudrive.utils.FileChunks;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.FileNotFoundException;


//    @Secured({"ADMIN"})
//    @PreAuthorize()


@RestController
@RequestMapping("/api/preview")
public class PreviewController {
    private final FilesService filesService;
    private final HttpSession session;

    @Value("${cloudrive.storage.directory}")
    private String STORAGE;

    @Value("${cloudrive.storage.tempfilder}")
    private String TEMP_FOLDER;

    @Autowired
    public PreviewController(FilesService filesService, HttpSession session) {
        this.filesService = filesService;
        this.session = session;
    }


    /**
     * Controller returns content of preview gif file
     * @param id Identificatior of file in database
     * @return Returns content of preview file. If file is not exist in database table return 404 (HttpStatus.NOT_FOUND). If can't
     * reads file returns 500 (HttpStatus.INTERNAL_SERVER_ERROR)
     */
    @GetMapping(value = "/{id}", produces = "image/gif")
    public ResponseEntity<InputStreamResource> getFile(@PathVariable("id") Long id, HttpServletRequest request, HttpServletResponse response) {
        User user = (User) session.getAttribute("user");
        FileEntity fileEntity = filesService.getFileById(user, id);

        if (fileEntity == null || fileEntity.getPreviews().size() < 1)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        try {
            return filesService.getFilePreviewResponse(fileEntity, user);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
