package ru.vasyunin.springcloudrive.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.vasyunin.springcloudrive.entity.FileItem;
import ru.vasyunin.springcloudrive.utils.FileChunkInfo;
import ru.vasyunin.springcloudrive.utils.FileChunks;
import ru.vasyunin.springcloudrive.dto.FileItemTDO;
import ru.vasyunin.springcloudrive.dto.FilelistDTO;
import ru.vasyunin.springcloudrive.entity.DirectoryItem;
import ru.vasyunin.springcloudrive.entity.User;
import ru.vasyunin.springcloudrive.service.DirectoryService;
import ru.vasyunin.springcloudrive.service.FilesService;
import ru.vasyunin.springcloudrive.service.RoleService;
import ru.vasyunin.springcloudrive.service.UserService;
import ru.vasyunin.springcloudrive.utils.FileUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.ws.http.HTTPException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ApiController {
    private final UserService userService;
    private final FilesService filesService;
    private final DirectoryService directoryService;
    private final RoleService roleService;

    @Value("${cloudrive.storage.directory}")
    private String STORAGE;

    @Value("${cloudrive.storage.tempfilder}")
    private String TEMP_FOLDER;

    @Autowired
    public ApiController(UserService userService, FilesService filesService, DirectoryService directoryService, RoleService roleService) {
        this.userService = userService;
        this.filesService = filesService;
        this.directoryService = directoryService;
        this.roleService = roleService;
    }

    /**
     * Getting list of files from user's directory
     * @return
     */
    @PostMapping("/filelist/directory/{id}")
    public FilelistDTO getFileListFromStorage(@PathVariable(name = "id", required = false)  Long dirId, HttpSession session){
        // Get User from session
        User user = (User)session.getAttribute("user");

        // Get current directory (root or required)
        DirectoryItem currentDirectory;

        if (dirId == null || dirId == 0){
            currentDirectory = directoryService.getDirectoryByParent(user, null);
        } else {
            currentDirectory = directoryService.getDirectoryById(user, dirId);
        }

        if (currentDirectory == null)
            return new FilelistDTO(Collections.emptyList(), dirId);

        List<FileItemTDO> result = filesService.getFilelistByDirectory(currentDirectory);

        return new FilelistDTO(result, dirId);
    }

    @PostMapping("/upload/chunk")
    public ResponseEntity uploadChunkOfFile(@RequestParam("file") MultipartFile file,
                                            @RequestParam("resumableRelativePath") long directory,
                                            HttpServletRequest request,
                                            HttpSession session) {

        // Get User from session
        User user = (User)session.getAttribute("user");

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
            FileItem fileItem = filesService.processChunkInDb(user, chunkInfo);

            filesService.processChunk(user, chunkInfo, file);
            chunks.addChunk(chunkInfo);

            // If file is downloaded set complited in FileItem
            if (chunks.isDone(chunkInfo)) {
                filesService.setFileComplited(fileItem);
            }
        }
        return new ResponseEntity(HttpStatus.OK);
    }


}

//
//        Path path = Paths.get(STORAGE + "/" + user.getId());
//        if (!path.toFile().exists() && checkDir) {
//            try {
//                Files.createDirectories(path);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        List<FileItemTDO> result = new ArrayList<>();
//        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)){
//            stream.forEach(p -> {
//                if (!Files.isDirectory(p)) {
//                    try {
//                        // Creating FileItem DTO
//                        FileItemTDO item = new FileItemTDO(0L,
//                                p.getFileName().toString(),
//                                Files.size(p),
//                                FileUtils.getFileExtension(p),
//                                LocalDateTime.ofInstant(Files.getLastModifiedTime(p).toInstant(), ZoneId.systemDefault()));
//                        result.add(item);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
