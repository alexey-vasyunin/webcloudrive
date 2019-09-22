package ru.vasyunin.springcloudrive.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.vasyunin.springcloudrive.entity.FileItem;
import ru.vasyunin.springcloudrive.entity.User;
import ru.vasyunin.springcloudrive.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@RestController
public class FilesController {
    private UserService userService;

    @Value("${cloudrive.storage.directory}")
    private String STORAGE;

    @Value("${cloudrive.storage.create_if_not_exists}")
    private boolean checkDir;

//    @Secured({"ADMIN"})
//    @PostMapping("/api/filelist")
    @GetMapping("/api/filelist")
    public List<FileItem> getFileListFromStorage(HttpServletRequest httpServletRequest){
        HttpSession session = httpServletRequest.getSession();

        // Get User from session
        User user = (User)session.getAttribute("user");
        System.out.println(user);

        Path path = Paths.get(STORAGE + "/" + user.getId());
        if (!path.toFile().exists() && checkDir) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println(path.toAbsolutePath());

        List<FileItem> result = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)){
            stream.forEach(p -> {
                if (!Files.isDirectory(p)) {
                    try {
                        FileItem item = new FileItem(0L, p.getFileName().toString(), Files.size(p), Files.probeContentType(p));
                        System.out.println(item);
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
