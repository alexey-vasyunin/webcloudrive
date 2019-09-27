package ru.vasyunin.springcloudrive.controller;

import com.sun.deploy.net.HttpResponse;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.vasyunin.springcloudrive.dto.FileItemTDO;
import ru.vasyunin.springcloudrive.entity.DirectoryItem;
import ru.vasyunin.springcloudrive.entity.User;
import ru.vasyunin.springcloudrive.service.DirectoryService;
import ru.vasyunin.springcloudrive.service.FilesService;
import ru.vasyunin.springcloudrive.service.RoleService;
import ru.vasyunin.springcloudrive.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ApiController {
    private final UserService userService;
    private final FilesService filesService;
    private final DirectoryService directoryService;
    private final RoleService roleService;

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
    public List<FileItemTDO> getFileListFromStorage(@PathVariable(name = "id", required = false)  Long dirId, HttpServletRequest httpServletRequest){
        HttpSession session = httpServletRequest.getSession();

        // Get User from session
        User user = (User)session.getAttribute("user");

        // Get current directory (root or required)
        DirectoryItem currentDirectory;

        if (dirId == null || dirId == 0){
            currentDirectory = directoryService.getDirectoryByParent(user, null);
        } else {
            currentDirectory = directoryService.getDirectoryById(user, dirId);
        }

        if (currentDirectory == null) return Collections.emptyList();

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
                .map(file -> {
                    return new FileItemTDO(file.getId(), file.getOriginFilename(), file.getSize(), file.getType(), file.getLast_modified(), false);
                }).collect(Collectors.toList()));

        return result;
    }

    @PostMapping("/upload/chunk")
    public ResponseEntity uploadChunkOfFile(HttpServletRequest request){
        System.out.println(request);

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
