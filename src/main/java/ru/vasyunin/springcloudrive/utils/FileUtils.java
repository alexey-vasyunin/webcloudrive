package ru.vasyunin.springcloudrive.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class FileUtils {

    /**
     * Method detect file extension
     * @param filename String with filename
     * @return Method returns filename extension if exists or empty string
     */
    public static String getFileExtension(String filename){
        return Optional.ofNullable(filename)
                .filter(s -> s.contains("."))
                .map(s -> s.substring(s.lastIndexOf(".") + 1))
                .orElse("");
    }


    /**
     * Method detect file extension
     * @param path Path of file
     * @return Method returns filename extension if exists or empty string
     */
    public static String getFileExtension(Path path){
        return getFileExtension(path.getFileName().toString());
    }

    /**
     * Function create folder if not exists
     * @param path Path of new folder
     * @return Path of new folder
     */
    public static Optional<Path> createSubfolder(Path path){
        if (!Files.exists(path)) {
            try {
                return Optional.of(Files.createDirectories(path));
            } catch (IOException e) {
                e.printStackTrace();
                return Optional.empty();
            }
        }
        return Optional.of(path);
    }

    public static Optional<Path> createSubfolder(String path){
        return createSubfolder(Paths.get(path));
    }
}
