package ru.vasyunin.springcloudrive.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.vasyunin.springcloudrive.service.UserService;

@RestController
@RequestMapping("/file")
public class FilesController {
    private UserService userService;

    @Value("${cloudrive.storage.directory}")
    private String STORAGE;

    @Value("${cloudrive.storage.create_if_not_exists}")
    private boolean checkDir;

//    @Secured({"ADMIN"})
//    @PreAuthorize()


//    /**
//     * Контроллер возвращает содержимое файла по его идентификатору.
//     * @param id Идентифткатор файла в базе
//     * @return Возвращает тело файла. Если файла нет в базе, то выдаёт ответ 404 (HttpStatus.NOT_FOUND), если не удалось
//     * вытащить файл, то ответ 500 (HttpStatus.INTERNAL_SERVER_ERROR)
//     */
//    @GetMapping(value = "/file/download", produces = "application/octet-stream")
//    public ResponseEntity<InputStreamResource> getFile(@PathVariable("id") Long id) {
//        Track track = trackService.findTrackById(id);
//        if (track == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
//
//        try {
//            return storage.getTrackFile(track);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }



    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
