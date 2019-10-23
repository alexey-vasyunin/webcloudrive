package ru.vasyunin.springcloudrive.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.vasyunin.springcloudrive.entity.FileItem;
import ru.vasyunin.springcloudrive.entity.User;
import ru.vasyunin.springcloudrive.service.FilesService;
import ru.vasyunin.springcloudrive.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/file")
public class FilesController {
    private final UserService userService;
    private final FilesService filesService;

    @Value("${cloudrive.storage.directory}")
    private String STORAGE;

    @Value("${cloudrive.storage.create_if_not_exists}")
    private boolean checkDir;


    @Autowired
    public FilesController(UserService userService, FilesService filesService) {
        this.userService = userService;
        this.filesService = filesService;
    }


//    @Secured({"ADMIN"})
//    @PreAuthorize()


    /**
     * Controller returns file content
     * @param id Identificatior of file in database
     * @return Returns content of file. If file is not exist in database table return 404 (HttpStatus.NOT_FOUND). If can't
     * reads file returns 500 (HttpStatus.INTERNAL_SERVER_ERROR)
     */
    @GetMapping(value = "/download/{id}", produces = "application/octet-stream")
    public ResponseEntity<InputStreamResource> getFile(@PathVariable("id") Long id, HttpServletRequest request, HttpServletResponse response) {
        User user = (User)request.getSession().getAttribute("user");
        System.out.println(Collections.list(request.getHeaderNames()) // log headers
                .stream()
                .map(s -> s + ": " + request.getHeader(s))
                .collect(Collectors.joining(System.lineSeparator())));
        FileItem fileItem = filesService.getFileById(user, id);

        if (fileItem == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        try {
            return filesService.getFileResponse(fileItem, user);
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
    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity deleteFile(@PathVariable("id") Long id, HttpServletRequest request){
        if (id == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        User user = (User)request.getSession().getAttribute("user");
        if (filesService.deleteFile(user, id))
            return ResponseEntity.ok().build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

}
