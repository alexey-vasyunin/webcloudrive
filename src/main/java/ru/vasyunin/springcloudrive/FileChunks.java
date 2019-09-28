package ru.vasyunin.springcloudrive;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Class for storing information about downloaded chunks of files
 */
public class FileChunks {
    private HashMap<String, Long> chunks;

    public FileChunks() {
        chunks = new HashMap<>();
    }

    /**
     * Check if number of stored chunks equals max number of chunk for file
     * @param filename Filename
     * @param max Maximum chunk number
     * @return {@code true} if all chunks is downloaded
     */
    public boolean isDone(String filename, long max){
        if (filename == null || "".equals(filename)) return false;
        return chunks.entrySet().stream()
                .filter(chunk -> filename.equals(chunk.getKey()))
                .count() == max;
    }

    /**
     * Store information about chunk of file
     * @param filename Name of file
     * @param chunkNumber Number of chunk
     * @return {@code true} if chunk didn't present in the hash storage
     */
    public boolean addChunk(String filename, Long chunkNumber){
        return chunks.putIfAbsent(filename, chunkNumber) == null;
    }

    /**
     * Clean all information about file chunks from memory
     * @param filename Uniq name of file
     */
    public void cleanByFilename(String filename){
        chunks.entrySet().stream()
                .filter(chunks->filename.equals(chunks.getKey()))
                .forEach(s -> chunks.remove(s.getKey()));
    }

    @Override
    public String toString() {
        return "FileChunks: " + chunks.entrySet().stream().sorted().map(s -> s.getKey() + ": " + s.getValue()).collect(Collectors.joining(System.lineSeparator()));
    }
}
