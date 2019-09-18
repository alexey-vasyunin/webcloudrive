package ru.vasyunin.springcloudrive.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.vasyunin.springcloudrive.UserPrincipal;
import ru.vasyunin.springcloudrive.entity.FileItem;
import ru.vasyunin.springcloudrive.entity.User;
import ru.vasyunin.springcloudrive.service.UserService;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.Collections;
import java.util.List;

@RestController
public class FilesController {
    private UserService userService;
    @Value("${cloudrive.storage.directory}")
    private static String STORAGE;

//    @PostMapping("/api/filelist")
    @GetMapping("/api/filelist")
    public List<FileItem> getFileListFromStorage(Principal principal){
        User user = userService.getUserByUsername(principal.getName());
        Path path = Paths.get(STORAGE + "/" + user.getId());
//        System.out.println(path.toAbsolutePath());
//        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)){
//            stream.forEach(path1 -> {
//                if (!path1.toFile().isDirectory()) System.out.println(path1.getFileName());
//            });
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        return Collections.emptyList();
    }


    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
