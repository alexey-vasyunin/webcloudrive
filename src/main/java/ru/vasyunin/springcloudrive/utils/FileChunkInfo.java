package ru.vasyunin.springcloudrive.utils;

import lombok.Data;

import javax.servlet.http.HttpServletRequest;

@Data
public class FileChunkInfo {
    private int chunkSize;
    private long totalSize;
    private String identifier;
    private String filename;
    private String relativePath;
    private long chunkNumber;
    private int totalChunks;

    public FileChunkInfo(HttpServletRequest request) {
        chunkSize = Integer.parseInt(request.getParameter("resumableChunkSize"));
        totalSize = Long.parseLong(request.getParameter("resumableTotalSize"));
        chunkNumber = Long.parseLong(request.getParameter("resumableChunkNumber"));
        identifier = request.getParameter("resumableIdentifier");
        filename = request.getParameter("resumableFilename");
        relativePath = request.getParameter("resumableRelativePath");
        totalChunks = (int) Math.ceil(((double)(totalSize/ chunkSize)));
    }
}
