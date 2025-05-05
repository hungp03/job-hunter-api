package com.example.jobhunter.service;

import com.example.jobhunter.dto.response.file.ResUploadFileDTO;
import com.example.jobhunter.util.error.StorageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@Service
public class FileService {

    @Value("${hp.upload-file.base-uri}")
    private String baseURI;

    public ResUploadFileDTO uploadFile(MultipartFile file, String folder) {
        if (file == null || file.isEmpty()) {
            throw new StorageException("File is empty. Please choose a file");
        }

        String fileName = file.getOriginalFilename();
        List<String> allowedExtensions = Arrays.asList("pdf", "jpg", "jpeg", "png", "doc", "docx");
        boolean isValid = allowedExtensions.stream().anyMatch(ext ->
                fileName != null && fileName.toLowerCase().endsWith(ext));

        if (!isValid) {
            throw new StorageException("File not allowed! Please use file " + allowedExtensions);
        }

        try {
            createDirectory(baseURI + folder);
            String uploadedFileName = store(file, folder);
            return new ResUploadFileDTO(uploadedFileName, Instant.now());
        } catch (IOException | URISyntaxException e) {
            throw new StorageException("Failed to upload file: " + e.getMessage());
        }
    }

    public ResponseEntity<Resource> downloadFile(String fileName, String folder)
            throws FileNotFoundException, URISyntaxException {

        if (fileName == null || folder == null) {
            throw new StorageException("Missing required params : (fileName or folder) in query params.");
        }

        long fileLength = getFileLength(fileName, folder);
        if (fileLength == 0) {
            throw new StorageException("File with name = " + fileName + " not found.");
        }

        InputStreamResource resource = getResource(fileName, folder);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentLength(fileLength)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    private void createDirectory(String folder) throws URISyntaxException, IOException {
        URI uri = new URI(folder);
        Path path = Paths.get(uri);
        File tmpDir = new File(path.toString());
        if (!tmpDir.isDirectory()) {
            Files.createDirectory(tmpDir.toPath());
        }
    }

    private String store(MultipartFile file, String folder) throws IOException, URISyntaxException {
        String originalFileName = file.getOriginalFilename();
        String finalName = System.currentTimeMillis() + "-" + originalFileName;
        String encodedFileName = URLEncoder.encode(finalName, "UTF-8").replace("+", "%20");
        URI uri = URI.create(baseURI + folder + "/" + encodedFileName);
        Path path = Paths.get(uri);
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
        }
        return finalName;
    }

    private long getFileLength(String fileName, String folder) throws URISyntaxException {
        URI uri = new URI(baseURI + folder + "/" + fileName);
        Path path = Paths.get(uri);
        File file = new File(path.toString());
        if (!file.exists() || file.isDirectory()) return 0;
        return file.length();
    }

    private InputStreamResource getResource(String fileName, String folder)
            throws URISyntaxException, FileNotFoundException {
        URI uri = new URI(baseURI + folder + "/" + fileName);
        Path path = Paths.get(uri);
        File file = new File(path.toString());
        return new InputStreamResource(new FileInputStream(file));
    }
}
