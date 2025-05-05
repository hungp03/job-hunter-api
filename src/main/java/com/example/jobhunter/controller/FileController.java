package com.example.jobhunter.controller;

import com.example.jobhunter.dto.response.file.ResUploadFileDTO;
import com.example.jobhunter.service.FileService;
import com.example.jobhunter.util.annotation.ApiMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;

@RestController
@RequestMapping("api")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping("files")
    @ApiMessage("Upload single file")
    public ResponseEntity<ResUploadFileDTO> upload(@RequestParam("file") MultipartFile file,
                                                   @RequestParam("folder") String folder) throws URISyntaxException, IOException {
        ResUploadFileDTO response = fileService.uploadFile(file, folder);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/files")
    @ApiMessage("Download a file")
    public ResponseEntity<Resource> download(@RequestParam("fileName") String fileName,
                                             @RequestParam("folder") String folder)
            throws URISyntaxException, FileNotFoundException {
        return fileService.downloadFile(fileName, folder);
    }
}