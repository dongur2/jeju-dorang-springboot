package com.donguri.jejudorang.domain.board.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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
        String extension = originalName.substring(originalName.lastIndexOf(".") + 1); // 파일 확장자 (png..)
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        String saveFileName = uuid + "." + extension;
        String fileFullPath = Paths.get(uploadDir, saveFileName).toString();

        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        try {
            File uploadFile = new File(fileFullPath);
            if (!uploadFile.getParentFile().exists()) {
                uploadFile.getParentFile().mkdirs(); // Create parent directories if they do not exist
            }
            image.transferTo(uploadFile); // 서버에 저장
            return saveFileName; // 서버에 저장된 파일명
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping(value = "/img-print", produces = { MediaType.IMAGE_GIF_VALUE, MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE })
    public byte[] printEditorImage(@RequestParam("filename") final String filename) {
        String fileFullPath = Paths.get(uploadDir, filename).toString();

        File uploadedFile = new File(fileFullPath);
        if (!uploadedFile.exists()) {
            throw new RuntimeException();
        }

        try {
            return Files.readAllBytes(uploadedFile.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
