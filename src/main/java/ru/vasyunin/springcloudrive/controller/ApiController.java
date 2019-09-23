package ru.vasyunin.springcloudrive.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.vasyunin.springcloudrive.dto.FileItemTDO;
import ru.vasyunin.springcloudrive.entity.User;
import ru.vasyunin.springcloudrive.service.UserService;
import ru.vasyunin.springcloudrive.utils.FileUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ApiController {
    private UserService userService;

    @Value("${cloudrive.storage.directory}")
    private String STORAGE;

    @Value("${cloudrive.storage.create_if_not_exists}")
    private boolean checkDir;

//    @Secured({"ADMIN"})
//    @PreAuthorize()


    /**
     * Getting list of files from user's directory
     * @return
     */
    @PostMapping("/filelist")
    public List<FileItemTDO> getFileListFromStorage(HttpServletRequest httpServletRequest){
        HttpSession session = httpServletRequest.getSession();

        // Get User from session
        User user = (User)session.getAttribute("user");

        Path path = Paths.get(STORAGE + "/" + user.getId());
        if (!path.toFile().exists() && checkDir) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        List<FileItemTDO> result = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)){
            stream.forEach(p -> {
                if (!Files.isDirectory(p)) {
                    try {
                        // Creating FileItem DTO
                        FileItemTDO item = new FileItemTDO(0L,
                                p.getFileName().toString(),
                                Files.size(p),
                                FileUtils.getFileExtension(p),
                                LocalDateTime.ofInstant(Files.getLastModifiedTime(p).toInstant(), ZoneId.systemDefault()));
                        result.add(item);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }



    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
