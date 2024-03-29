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
@RequestMapping("/api/file")
public class FilesController {
    private final FilesService filesService;
    private final HttpSession session;

    @Value("${cloudrive.storage.directory}")
    private String STORAGE;

    @Value("${cloudrive.storage.tempfilder}")
    private String TEMP_FOLDER;

    @Autowired
    public FilesController(FilesService filesService, HttpSession session) {
        this.filesService = filesService;
        this.session = session;
    }


    /*
           Upload file (by chunks)
     */
    @PostMapping
    public ResponseEntity uploadChunkOfFile(@RequestParam("file") MultipartFile file,
                                            HttpServletRequest request) {
        User user = (User) session.getAttribute("user");

        synchronized (user) {
            // Get information about downloaded chunks
            FileChunks chunks = (FileChunks) session.getAttribute("chunks");
            if (chunks == null) {
                chunks = new FileChunks();
                session.setAttribute("chunks", chunks);
            }

            // Get info about chunk from request
            FileChunkInfo chunkInfo = new FileChunkInfo(request);
            // Save fileinfo in database
            FileEntity fileEntity = filesService.processChunk(user, chunkInfo, file);

            chunks.addChunk(chunkInfo);

            // If file is downloaded set complited in FileEntity
            if (chunks.isDone(chunkInfo)) {
                filesService.setFileComplited(fileEntity);
                filesService.createPreviewFile(user, fileEntity);
            }
        }
        return new ResponseEntity(HttpStatus.OK);
    }


    /**
     * Controller returns file content
     * @param id Identificatior of file in database
     * @return Returns content of file. If file is not exist in database table return 404 (HttpStatus.NOT_FOUND). If can't
     * reads file returns 500 (HttpStatus.INTERNAL_SERVER_ERROR)
     */
    @GetMapping(value = "/{id}", produces = "application/octet-stream")
    public ResponseEntity<InputStreamResource> getFile(@PathVariable("id") Long id, HttpServletRequest request, HttpServletResponse response) {
        User user = (User) session.getAttribute("user");
        FileEntity fileEntity = filesService.getFileById(user, id);

        if (fileEntity == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        try {
            return filesService.getFileResponse(fileEntity, user);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }



    /**
     * Delete file
     * @param id
     * @param request
     * @return
     */
    @DeleteMapping
    public ResponseEntity deleteFile(@RequestParam Long id, HttpServletRequest request){
        if (id == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        User user = (User) session.getAttribute("user");

        if (filesService.deleteFile(user, id))
            return ResponseEntity.ok().build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PutMapping
    public ResponseEntity renameFile(@RequestParam Long id, @RequestParam String name){
        if (name.trim().equals("")) return ResponseEntity.badRequest().build();

        User user = (User) session.getAttribute("user");
        if (filesService.updateFile(user, id, name))
            return ResponseEntity.ok().build();
        else
            return ResponseEntity.badRequest().build();
    }
}
