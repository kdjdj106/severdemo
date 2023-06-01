package com.example.severdemo.service;

import com.example.severdemo.ImageDTO;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
@Log4j2
public class FileService {

    @Transactional
    public ImageDTO createImageDTO(String originalSourceName, Path path) throws IOException {
        String fileName = originalSourceName.substring(originalSourceName.lastIndexOf("\\") + 1);
        String uuid = UUID.randomUUID().toString();
        String fileUrl = getDirectory(path) + File.separator + uuid + "_" + fileName;

        return ImageDTO.builder()
                .fileName(fileName)
                .uuid(uuid)
                .fileUrl(fileUrl)
                .build();
    }

    private String getDirectory(Path path) throws IOException {
        if(!Files.exists(path)) {
            Files.createDirectory(path);
        }
        return path.toString();
    }
}
