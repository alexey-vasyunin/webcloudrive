package ru.vasyunin.springcloudrive.utils;

import lombok.Data;
import lombok.Getter;

import javax.servlet.http.HttpServletRequest;
import javax.swing.*;

@Getter
public class FileChunkInfo {
    private final int chunkSize;
    private final long totalSize;
    private final String identifier;
    private final String filename;
    private final String relativePath;
    private final long chunkNumber;
    private final int totalChunks;
    private final long offset;

    public FileChunkInfo(HttpServletRequest request) {
        chunkSize = Integer.parseInt(request.getParameter("resumableChunkSize"));
        totalSize = Long.parseLong(request.getParameter("resumableTotalSize"));
        chunkNumber = Long.parseLong(request.getParameter("resumableChunkNumber"));
        identifier = request.getParameter("resumableIdentifier");
        filename = request.getParameter("resumableFilename");
        relativePath = request.getParameter("resumableRelativePath");
        totalChunks = (int) Math.ceil(((double)(totalSize/ chunkSize)));
        offset = (chunkNumber - 1) * chunkSize;
    }
}
