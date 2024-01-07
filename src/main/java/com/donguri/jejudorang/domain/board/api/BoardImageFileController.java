package com.donguri.jejudorang.domain.board.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/tui-editor")
public class BoardImageFileController {

    @Value("${file.upload-directory}")
    private String uploadDir;

    @PostMapping("/img-upload")
    public String uploadEditorImage(@RequestParam("image") final MultipartFile image) {
        if (image.isEmpty()) {
            return "";
        }

        String originalName = image.getOriginalFilename();
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        String extension = originalName.substring(originalName.lastIndexOf(".") + 1);
        String saveFileName = uuid + "." + extension;
        String fileFullPath = Paths.get(uploadDir, saveFileName).toString();

        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        try {
            File uploadFile = new File(fileFullPath);
            image.transferTo(uploadFile);
            return saveFileName;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
