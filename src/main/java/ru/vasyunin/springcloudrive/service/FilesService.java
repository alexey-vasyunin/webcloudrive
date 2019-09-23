package ru.vasyunin.springcloudrive.service;

import org.springframework.stereotype.Service;
import ru.vasyunin.springcloudrive.dto.FileItemTDO;
import ru.vasyunin.springcloudrive.entity.User;
import ru.vasyunin.springcloudrive.repository.DirectoryRepository;
import ru.vasyunin.springcloudrive.repository.FilesRepository;
import ru.vasyunin.springcloudrive.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilesService {
    private final FilesRepository filesRepository;
    private final DirectoryRepository directoryRepository;
    private final UserRepository userRepository;

    public FilesService(FilesRepository filesRepository, DirectoryRepository directoryRepository, UserRepository userRepository) {
        this.filesRepository = filesRepository;
        this.directoryRepository = directoryRepository;
        this.userRepository = userRepository;
    }

    public List<FileItemTDO> getFilesInDirectory(User user, Long id) {
        return filesRepository.findAllByUser(user).stream().map(file -> {
            return new FileItemTDO(file.getId(), file.getOriginFilename(), file.getSize(), file.getType(), file.getLast_modified(), false);
        }).collect(Collectors.toList());
    }
}
