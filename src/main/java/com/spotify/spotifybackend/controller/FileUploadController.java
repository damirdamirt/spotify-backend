package com.spotify.spotifybackend.controller;

import com.spotify.spotifybackend.service.FileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
public class FileUploadController {

    private final FileService fileService;

    public FileUploadController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("checksum") String checksum // Ovo nam salje Frontend (SHA-256)
    ) {
        try {
            // Pozivamo nas servis koji radi Magic Number i Hash proveru
            String savedFileName = fileService.saveFile(file, checksum);
            return ResponseEntity.ok("File uploaded successfully: " + savedFileName);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Server error: " + e.getMessage());
        }
    }
}
