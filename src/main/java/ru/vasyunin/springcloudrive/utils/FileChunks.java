package ru.vasyunin.springcloudrive.utils;

import java.util.HashSet;
import java.util.stream.Collectors;

/**
 * Class for storing information about downloaded chunks of files
 */
public class FileChunks {

    public static class Chunk {
        public final String id;
        public final long chunkNumber;

        public Chunk(String id, long chunkNumber) {
            this.id = id;
            this.chunkNumber = chunkNumber;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Chunk chunk = (Chunk) o;

            if (chunkNumber != chunk.chunkNumber) return false;
            return id.equals(chunk.id);
        }

        @Override
        public int hashCode() {
            int result = id.hashCode();
            result = 31 * result + (int) (chunkNumber ^ (chunkNumber >>> 32));
            return result;
        }
    }

    private HashSet<Chunk> chunks;

    public FileChunks() {
        chunks = new HashSet<>();
    }

    /**
     * Check if number of stored chunks equals max number of chunk for file
     * @param id Filename
     * @param max Maximum chunk number
     * @return {@code true} if all chunks is downloaded
     */
    public boolean isDone(String id, long max){
        if (id == null || "".equals(id)) return false;
        long count = chunks.stream()
                .filter(chunk -> id.equals(chunk.id))
                .count();
        return count == max;
    }

    public boolean isDone(String filename, FileChunkInfo fileChunkInfo){
        return isDone(filename, fileChunkInfo.totalChunks);
    }

    public boolean isDone(FileChunkInfo fileChunkInfo){
        return isDone(fileChunkInfo.identifier, fileChunkInfo.totalChunks);
    }

    /**
     * Store information about chunk of file
     * @param filename Name of file
     * @param chunkNumber Number of chunk
     * @return {@code true} if chunk didn't present in the hash storage
     */
    public boolean addChunk(String filename, Long chunkNumber){
        Chunk chunk = new Chunk(filename, chunkNumber);
        return chunks.add(chunk);
    }

    public boolean addChunk(String filename, FileChunkInfo fileChunkInfo){
        return addChunk(filename, fileChunkInfo.chunkNumber);
    }

    public boolean addChunk(FileChunkInfo fileChunkInfo){
        return addChunk(fileChunkInfo.identifier, fileChunkInfo.chunkNumber);
    }
    /**
     * Clean all information about file chunks from memory
     * @param id Uniq name of file
     */
    public void cleanByFilename(String id){
        chunks.removeIf(chunk -> id.equals(chunk.id));
    }

    @Override
    public String toString() {
        return "FileChunks: " + chunks.stream().map(s -> s.id + ": " + s.chunkNumber).collect(Collectors.joining(System.lineSeparator()));
    }
}
