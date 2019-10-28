package ru.vasyunin.springcloudrive.utils;

import lombok.Data;
import lombok.Getter;

import javax.servlet.http.HttpServletRequest;
import javax.swing.*;
import java.util.UUID;

public class FileChunkInfo {
    public final int chunkSize;
    public final long totalSize;
    public final String identifier;
    public final String filename;
    public final long relativePath;
    public final long chunkNumber;
    public final int totalChunks;
    public final long offset;
    public final String localFilename;

    public FileChunkInfo(HttpServletRequest request) {
        chunkSize = Integer.parseInt(request.getParameter("resumableChunkSize"));
        totalSize = Long.parseLong(request.getParameter("resumableTotalSize"));
        chunkNumber = Long.parseLong(request.getParameter("resumableChunkNumber"));
        relativePath = Long.parseLong(request.getParameter("resumableRelativePath"));
        identifier = request.getParameter("resumableIdentifier");
        filename = request.getParameter("resumableFilename");
        localFilename = UUID.nameUUIDFromBytes((identifier + relativePath).getBytes()).toString();
        totalChunks = Integer.parseInt(request.getParameter("resumableTotalChunks"));
        offset = (chunkNumber - 1) * chunkSize;
    }

    @Override
    public String toString() {
        return "FileChunkInfo{" +
                "chunkSize=" + chunkSize +
                ", totalSize=" + totalSize +
                ", chunkNumber=" + chunkNumber +
                ", totalChunks=" + totalChunks +
                '}';
    }
}
