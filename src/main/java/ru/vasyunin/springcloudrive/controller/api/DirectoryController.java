package ru.vasyunin.springcloudrive.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.vasyunin.springcloudrive.dto.FileItemDto;
import ru.vasyunin.springcloudrive.dto.FilelistDto;
import ru.vasyunin.springcloudrive.entity.DirectoryItem;
import ru.vasyunin.springcloudrive.entity.User;
import ru.vasyunin.springcloudrive.service.DirectoryService;
import ru.vasyunin.springcloudrive.service.FilesService;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;

@RestController
@Transactional
@RequestMapping("/api/directory")
public class DirectoryController {

    private final FilesService filesService;
    private final DirectoryService directoryService;
    private final HttpSession session;

    @Value("${cloudrive.storage.directory}")
    private String STORAGE;

    @Value("${cloudrive.storage.tempfilder}")
    private String TEMP_FOLDER;

    @Autowired
    public DirectoryController(FilesService filesService, DirectoryService directoryService, HttpSession session) {
        this.filesService = filesService;
        this.directoryService = directoryService;
        this.session = session;
    }

    /**
     * Getting list of files from user's directory
     * @return
     */
    @GetMapping("/{id}")
    public FilelistDto getFileListFromStorage(@PathVariable(name = "id", required = false)  Long dirId){
        // Get current directory (root or required)
        DirectoryItem currentDirectory;

        User user = (User) session.getAttribute("user");

        if (dirId == null || dirId == 0){
            currentDirectory = directoryService.getRootDirectory(user);
        } else {
            currentDirectory = directoryService.getDirectoryById(user, dirId);
        }

        if (currentDirectory == null)
            return new FilelistDto(Collections.emptyList(), dirId);

        List<FileItemDto> result = filesService.getFilelistByDirectory(currentDirectory);

        return new FilelistDto(result, dirId);
    }



    @DeleteMapping
    public ResponseEntity deleteDirectory(@RequestParam("id") Long id) {
        User user = (User) session.getAttribute("user");
        directoryService.deleteDirectory(user, id);
        return ResponseEntity.ok().build();
    }


    @PostMapping
    public ResponseEntity newDirectory(@RequestParam("id") Long id, @RequestParam("name") String name){
        User user = (User) session.getAttribute("user");
        DirectoryItem parent = directoryService.getDirectoryById(user, id);
        if (parent == null) return ResponseEntity.badRequest().build();

        directoryService.addNewDirectory(user, parent.getId(), name);

        return ResponseEntity.ok().build();
    }

    @PutMapping
    public ResponseEntity renameDirectory(@RequestParam Long id, @RequestParam String name){
        if (name.trim().equals("")) return ResponseEntity.badRequest().build();

        User user = (User) session.getAttribute("user");
        if (directoryService.updateDirectory(user, id, name))
            return ResponseEntity.ok().build();
        else
            return ResponseEntity.badRequest().build();
    }
}
