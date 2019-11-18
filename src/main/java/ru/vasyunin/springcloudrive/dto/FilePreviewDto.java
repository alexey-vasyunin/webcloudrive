package ru.vasyunin.springcloudrive.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.vasyunin.springcloudrive.entity.FileEntity;
import ru.vasyunin.springcloudrive.entity.FilePreview;
import ru.vasyunin.springcloudrive.utils.FileType;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class FilePreviewDto {
    private List<String> filenames;

    public FilePreviewDto(FileEntity file) {
        filenames = file.getPreviews()
                .stream()
                .map(FilePreview::getFilename)
                .collect(Collectors.toList());
    }
}
