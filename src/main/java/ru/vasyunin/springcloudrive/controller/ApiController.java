package ru.vasyunin.springcloudrive.controller;

import com.sun.deploy.net.HttpResponse;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.vasyunin.springcloudrive.FileChunks;
import ru.vasyunin.springcloudrive.dto.FileItemTDO;
import ru.vasyunin.springcloudrive.dto.FilelistDTO;
import ru.vasyunin.springcloudrive.entity.DirectoryItem;
import ru.vasyunin.springcloudrive.entity.FileItem;
import ru.vasyunin.springcloudrive.entity.User;
import ru.vasyunin.springcloudrive.service.DirectoryService;
import ru.vasyunin.springcloudrive.service.FilesService;
import ru.vasyunin.springcloudrive.service.RoleService;
import ru.vasyunin.springcloudrive.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
    public FilelistDTO getFileListFromStorage(@PathVariable(name = "id", required = false)  Long dirId, HttpServletRequest httpServletRequest){
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

        if (currentDirectory == null) return new FilelistDTO(Collections.emptyList(), dirId);

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
                .filter(FileItem::isCompleted)
                .map(file -> {
                    return new FileItemTDO(file.getId(), file.getOriginFilename(), file.getSize(), file.getType(), file.getLast_modified(), false);
                }).collect(Collectors.toList()));

        return new FilelistDTO(result, dirId);
    }

    @PostMapping("/upload/chunk")
    public ResponseEntity uploadChunkOfFile(@RequestParam("file") MultipartFile file,
                                            HttpServletRequest request,
                                            HttpSession session) throws IOException {

        FileChunks chunks = (FileChunks) session.getAttribute("chunks");
        if (chunks == null){
            chunks = new FileChunks();
        }

        try {
            chunks.addChunk(file.getOriginalFilename(), Long.parseLong(request.getParameter("resumableChunkNumber")));
            int resumableChunkSize = Integer.parseInt(request.getParameter("resumableChunkSize"), -1);
            long resumableTotalSize = Long.parseLong(request.getParameter("resumableTotalSize"), -1);

            if (chunks.isDone(file.getOriginalFilename(), (long) Math.ceil((double) (resumableTotalSize / resumableChunkSize)))){
                //
                System.out.println("File is done!");
            }

        } catch (NumberFormatException e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }



        System.out.println("===============");
        System.out.println(request);
        System.out.println(file);
        System.out.println("getOriginalFilename " + file.getOriginalFilename());
        System.out.println("getSize " + file.getSize());
        System.out.println("getBytes " + file.getBytes().length);
        System.out.println("isEmpty " + file.isEmpty());
        System.out.println("getName " + file.getName());
        System.out.println("resumableChunkNumber " + request.getParameter("resumableChunkNumber"));
        System.out.println("resumableIdentifier " + request.getParameter("resumableIdentifier"));
        System.out.println("===============");
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
