package ru.vasyunin.springcloudrive.utils;

import java.nio.file.Path;
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
}
