package ru.vasyunin.springcloudrive.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.vasyunin.springcloudrive.entity.FileItem;
import ru.vasyunin.springcloudrive.entity.User;
import ru.vasyunin.springcloudrive.service.FilesService;
import ru.vasyunin.springcloudrive.service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;
import java.security.Principal;

@RestController
@RequestMapping("/file")
public class FilesController {
    private final UserService userService;
    private final FilesService filesService;

    @Value("${cloudrive.storage.directory}")
    private String STORAGE;

    @Value("${cloudrive.storage.create_if_not_exists}")
    private boolean checkDir;



//    @Secured({"ADMIN"})
//    @PreAuthorize()


    /**
     * Controller returns file content
     * @param id Identificatior of file in database
     * @return Returns content of file. If file is not exist in database table return 404 (HttpStatus.NOT_FOUND). If can't
     * reads file returns 500 (HttpStatus.INTERNAL_SERVER_ERROR)
     */
    @GetMapping(value = "/download/{id}", produces = "application/octet-stream")
    public ResponseEntity<InputStreamResource> getFile(@PathVariable("id") Long id, HttpServletRequest request) {
        User user = (User)request.getSession().getAttribute("user");
        FileItem fileItem = filesService.getFileById(user, id);
        if (fileItem == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        try {
            return filesService.getFile(fileItem, user);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @Autowired
    public FilesController(UserService userService, FilesService filesService) {
        this.userService = userService;
        this.filesService = filesService;
    }

}
